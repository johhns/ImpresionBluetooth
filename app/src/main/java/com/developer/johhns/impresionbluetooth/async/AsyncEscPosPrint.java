package com.developer.johhns.impresionbluetooth.async;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.dantsu.escposprinter.EscPosCharsetEncoding;
import com.dantsu.escposprinter.EscPosPrinter;
import com.dantsu.escposprinter.connection.DeviceConnection;
import com.dantsu.escposprinter.exceptions.EscPosBarcodeException;
import com.dantsu.escposprinter.exceptions.EscPosConnectionException;
import com.dantsu.escposprinter.exceptions.EscPosEncodingException;
import com.dantsu.escposprinter.exceptions.EscPosParserException;

import java.lang.ref.WeakReference;

public abstract class AsyncEscPosPrint extends AsyncTask<AsyncEscPosPrinter, Integer, Integer> {
    protected final static int FINISH_SUCCESS = 1;
    protected final static int FINISH_NO_PRINTER = 2;
    protected final static int FINISH_PRINTER_DISCONNECTED = 3;
    protected final static int FINISH_PARSER_ERROR = 4;
    protected final static int FINISH_ENCODING_ERROR = 5;
    protected final static int FINISH_BARCODE_ERROR = 6;

    protected final static int PROGRESS_CONNECTING = 1;
    protected final static int PROGRESS_CONNECTED = 2;
    protected final static int PROGRESS_PRINTING = 3;
    protected final static int PROGRESS_PRINTED = 4;

    String texto ;

    protected ProgressDialog dialog;
    protected WeakReference<Context> weakContext;


    public AsyncEscPosPrint(Context context) {
        this.weakContext = new WeakReference<>(context);
    }

    protected Integer doInBackground(AsyncEscPosPrinter... printersData) {
        if (printersData.length == 0) {
            return AsyncEscPosPrint.FINISH_NO_PRINTER;
        }

        this.publishProgress(AsyncEscPosPrint.PROGRESS_CONNECTING);

        AsyncEscPosPrinter printerData = printersData[0];

        try {
            DeviceConnection deviceConnection = printerData.getPrinterConnection();

            if(deviceConnection == null) {
                return AsyncEscPosPrint.FINISH_NO_PRINTER;
            }

            EscPosPrinter printer = new EscPosPrinter(
                    deviceConnection,
                    printerData.getPrinterDpi(),
                    printerData.getPrinterWidthMM(),
                    printerData.getPrinterNbrCharactersPerLine(),
                    new EscPosCharsetEncoding("windows-1252", 16)
            );

            this.publishProgress(AsyncEscPosPrint.PROGRESS_PRINTING);

            texto = printerData.getTextToPrint();

            printer.printFormattedTextAndCut(printerData.getTextToPrint());

            this.publishProgress(AsyncEscPosPrint.PROGRESS_PRINTED);

        } catch (EscPosConnectionException e) {
            e.printStackTrace();
            return AsyncEscPosPrint.FINISH_PRINTER_DISCONNECTED;
        } catch (EscPosParserException e) {
            e.printStackTrace();
            return AsyncEscPosPrint.FINISH_PARSER_ERROR;
        } catch (EscPosEncodingException e) {
            e.printStackTrace();
            return AsyncEscPosPrint.FINISH_ENCODING_ERROR;
        } catch (EscPosBarcodeException e) {
            e.printStackTrace();
            return AsyncEscPosPrint.FINISH_BARCODE_ERROR;
        }

        return AsyncEscPosPrint.FINISH_SUCCESS ;
    }

    protected void onPreExecute() {
        if (this.dialog == null) {
            Context context = weakContext.get();

            if (context == null) {
                return;
            }

            this.dialog = new ProgressDialog(context);
            this.dialog.setTitle("Imprimiendo...");
            this.dialog.setMessage("...");
            this.dialog.setProgressNumberFormat("%1d / %2d");
            this.dialog.setCancelable(false);
            this.dialog.setIndeterminate(false);
            this.dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            this.dialog.show();
        }
    }

    protected void onProgressUpdate(Integer... progress) {
        switch (progress[0]) {
            case AsyncEscPosPrint.PROGRESS_CONNECTING:
                this.dialog.setMessage("Conectandose al impresor ...");
                break;
            case AsyncEscPosPrint.PROGRESS_CONNECTED:
                this.dialog.setMessage("Impresor conectado...");
                break;
            case AsyncEscPosPrint.PROGRESS_PRINTING:
                this.dialog.setMessage("Impresor imprimiendo...");
                break;
            case AsyncEscPosPrint.PROGRESS_PRINTED:
                this.dialog.setMessage("La impresion ha finalizado...");
                break;
        }
        this.dialog.setProgress(progress[0]);
        this.dialog.setMax(4);
    }

    protected void onPostExecute(Integer result) {
        this.dialog.dismiss();
        this.dialog = null;

        Context context = weakContext.get();

        if (context == null) {
            return;
        }

        switch (result) {
            case AsyncEscPosPrint.FINISH_SUCCESS:
                new AlertDialog.Builder(context)
                        .setTitle("Success")
                        .setMessage("Impresion finalizada exitosamente"  )
                        .show();
                break;
            case AsyncEscPosPrint.FINISH_NO_PRINTER:
                new AlertDialog.Builder(context)
                        .setTitle("No printer")
                        .setMessage("No se encontro la impresora")
                        .show();
                break;
            case AsyncEscPosPrint.FINISH_PRINTER_DISCONNECTED:
                new AlertDialog.Builder(context)
                    .setTitle("Broken connection")
                    .setMessage("No se puede conectar a la impresora")
                    .show();
                break;
            case AsyncEscPosPrint.FINISH_PARSER_ERROR:
                new AlertDialog.Builder(context)
                    .setTitle("Formato de texto no valido")
                    .setMessage("Parece que hay problemas de sintaxis")
                    .show();
                break;
            case AsyncEscPosPrint.FINISH_ENCODING_ERROR:
                new AlertDialog.Builder(context)
                    .setTitle("Mala codificacion")
                    .setMessage("Los caracteres de codificacion seleccionados devuelven error.")
                    .show();
                break;
            case AsyncEscPosPrint.FINISH_BARCODE_ERROR:
                new AlertDialog.Builder(context)
                    .setTitle("Codigo de barras erroneo")
                    .setMessage("Los datos enviados para convertirse en c??digo de barras o c??digo QR parecen no son v??lidos.")
                    .show();
                break;
        }
    }
}
