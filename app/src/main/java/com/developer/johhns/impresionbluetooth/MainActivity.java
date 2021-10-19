package com.developer.johhns.impresionbluetooth;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.dantsu.escposprinter.connection.DeviceConnection;
import com.dantsu.escposprinter.connection.bluetooth.BluetoothConnection;
import com.dantsu.escposprinter.connection.bluetooth.BluetoothPrintersConnections;
import com.developer.johhns.impresionbluetooth.async.AsyncBluetoothEscPosPrint;
import com.developer.johhns.impresionbluetooth.async.AsyncEscPosPrinter;

import java.text.SimpleDateFormat;

public class MainActivity extends AppCompatActivity {

    public static final int PERMISSION_BLUETOOTH = 1;
    private BluetoothConnection selectedDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = (Button) this.findViewById(R.id.button_bluetooth_browse);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                browseBluetoothDevice();
            }
        });
        button = (Button) findViewById(R.id.button_bluetooth);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                printBluetooth();
            }
        });

    }




    public void browseBluetoothDevice() {
        final BluetoothConnection[] bluetoothDevicesList = (new BluetoothPrintersConnections()).getList();

        if (bluetoothDevicesList != null) {
            final String[] items = new String[bluetoothDevicesList.length + 1];
            items[0] = "Default printer";
            int i = 0;
            for (BluetoothConnection device : bluetoothDevicesList) {
                items[++i] = device.getDevice().getName();
            }
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
            alertDialog.setTitle("Bluetooth printer selection");
            alertDialog.setItems(items, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    int index = i - 1;
                    if(index == -1) {
                        selectedDevice = null;
                    } else {
                        selectedDevice = bluetoothDevicesList[index];
                    }
                    Button button = (Button) findViewById(R.id.button_bluetooth_browse);
                    button.setText(items[i]);
                }
            });
            AlertDialog alert = alertDialog.create();
            alert.setCanceledOnTouchOutside(false);
            alert.show();
        }
    }

    public void printBluetooth() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH}, MainActivity.PERMISSION_BLUETOOTH);
        } else {
            new AsyncBluetoothEscPosPrint(this).execute(this.getAsyncEscPosPrinter(selectedDevice));
        }
    }



    /**
     * Asynchronous printing
     */
    @SuppressLint("SimpleDateFormat")
    public AsyncEscPosPrinter getAsyncEscPosPrinter(DeviceConnection printerConnection) {
        SimpleDateFormat format = new SimpleDateFormat("'on' yyyy-MM-dd 'at' HH:mm:ss");
        /// envio de caña
        String textToPrint = "^XA" +
                "^SZ2" +
                "^MTD" +
                "^MNN" +
                "^MCY" +
                "~TA0" +
                "~JSN" +
                "^MD0" +
                "^LT0" +
                "^MFN,N" +
                "^JZY" +
                "^PR6,6,6" +
                "^POI" +
                "^ML400" +
                "^LL800" +
                "^PW725" +
                "^LH0,20" +
                "^LRN" +
                "^LH1,1" +
                "^FO50,15,0^AFN,20,7^FDINGENIO EL ANGEL, S.A DE C.V. ^FS" +
                "^FO50,40,0^AFN,20,7^FDENVIO DE CANA DE AZUCAR # 555555 ^FS" +
                "^FO50,65,0^ADN,11,7^FDFECHA:19/10/2021 ORDEN COSECHA: 12345 ^FS" +
                "^FO50,90,0^ADN,11,7^FDCONTRATO: 41-CR GON BUSINESS^FS" +
                "^FO50,115,0^ADN,11,7^FDPROPIEDAD: 111-RAYITAS LOTE: CONTA^FS" +
                "^FO50,140,0^ADN,11,7^FDTIPO CANA : ROYIZA-LARGA-CRUDA^FS" +
                "^FO50,165,0^ADN,11,7^FDDEPTO          : SAN SALVADOR MUN: APOPA^FS" +
                "^FO50,190,0^ADN,11,7^FDCANTON         : EL ANGEL VAR: MIX ^FS" +
                "^FO50,215,0^ADN,11,7^FDNUMERO DESPACHO: 555555^FS" +
                "^FO50,240,0^ADN,11,7^FDCARGADOR       : CGD01-PELUKIN^FS" +
                "^FO50,265,0^ADN,11,7^FDCARGADORA      : CRG-0033 PLACA: 33333 ^FS" +
                "^FO50,290,0^ADN,11,7^FDNOMBRE         : MACHADIN-JUNIOR^FS" +
                "^FO50,315,0^ADN,11,7^FDMOTORISTA      : CARLOS LOPEZ^FS" +
                "^FO50,340,0^ADN,11,7^FDF.QUEMA        : 01/10/2021^FS" +
                "^FO50,365,0^ADN,11,7^FDF.CORTE        : 02/10/2021^FS" +
                "^FO50,390,0^ADN,11,7^FDF.CARGA        : 03/10/2021^FS" +
                "^FO50,415,0^ADN,11,7^FDCARGADO DIFICIL: NO  LOTE TERMINADO: NO^FS" +
                "^FO50,440,0^ADN,11,7^FDCONTRATISTA    : SOTO REYES^FS" +
                "^FO50,475" +
                "^B7N,6,4,12" +
                "^FD" +
                "[{\"IDENV\":\"555555\" , \"FECHA_ENVIO\":\"19/10/2021\" , \"FECHA_DIGITACION\":\"19/10/2021\" , \"ESTADO\":\"PEN\" ," +
                "  \"TIPO_LARGO\":\"LARGA\" , \"TIPO_CANIA\":\"CRUDA\" , \"IDOCT\":\"1221\" , \"IDDSP\":\"44444\" ," +
                "  \"TIPO_QUEMA\":\"PROG\" , \"FECHA_QUEMA\":\"01/10/2021\" , \"FECHA_CORTE\":\"02/10/2021\" ," +
                "  \"FECHA_CARGA\":\"03/10/2021\" , \"OBSERVACIONES\":\"NINGUNA\" , \"IDCNT\":\"6000\" , \"NUMLOT\":\"1\" ," +
                "  \"NUMPPD\":\"10111\" , \"NUMTPE\":\"1\" , \"CODEQU\":\"CGD-0033\" , \"NUMCGD\":\"1\" ," +
                "  \"NUMERO_ENVIO\":\"1\" , \"NUMEQT\":\"3333\" , \"MODULO\":\"CAN\" , \"IDCTR\":\"2514\" ," +
                "  \"IDMOA\":\"1\" , \"CARGADO_DIFICIL\":\"N\" , \"CONTRATISTA\": \"\" , \"CREADO_POR\":\"1401\" ," +
                "  \"CREADO_EL\":\"03/10/2021\" , \"CREADO_EN\":\"01\" , \"SINCRONIZADO_EL\":\"03/10/2021\"  ," +
                "  \"ENVIO_HH\":\"140001\" , \"DESPACHO\":\"5555\"" +
                "}] ^FS" +
                "^XZ" ;

        /* Rozadores
        String textToPrint = "^XA" +
                "^SZ2" +
                "^MTD" +
                "^MNN" +
                "^MCY" +
                "~TA0" +
                "~JSN" +
                "^MD0" +
                "^LT0" +
                "^MFN,N" +
                "^JZY" +
                "^PR6,6,6" +
                "^ML200" +
                "^LL300" +
                "^LH0,20" +
                "^LRN" +
                "^LH1,1" +
                "^FO50,30" +
                "^B7N,4,5,8" +
                "^FD" +
                "[{\"ENVIO_HH\":\"1022131\",\"CORRELATIVO\":\"7\",\"NUMTAG\":\"20173\",\"GARRADAS\":\"21\",\"TIPO_TRABAJADOR\":\"R\",\"NUMTRC\":\"0\","+
                "\"SINCRONIZADO_EL\":\"26-02-2015 14:30:57\",\"CREADO_POR\":\"1481\"},{\"ENVIO_HH\":\"1022131\",\"CORRELATIVO\":\"8\",\"NUMTAG\":\"18758\","+
                "\"GARRADAS\":\"4\",\"TIPO_TRABAJADOR\":\"R\",\"NUMTRC\":\"0\",\"SINCRONIZADO_EL\":\"26-02-2015 14:30:58\",\"CREADO_POR\":\"1481\"},"+
                "{\"ENVIO_HH\":\"1022131\",\"CORRELATIVO\":\"9\",\"NUMTAG\":\"18208\",\"GARRADAS\":\"6\",\"TIPO_TRABAJADOR\":\"R\",\"NUMTRC\":\"0\","+
                "\"SINCRONIZADO_EL\":\"26-02-2015 14:30:58\",\"CREADO_POR\":\"1481\"},{\"ENVIO_HH\":\"1022131\",\"CORRELATIVO\":\"10\",\"NUMTAG\":\"18213\","+
                "\"GARRADAS\":\"8\",\"TIPO_TRABAJADOR\":\"R\",\"NUMTRC\":\"0\",\"SINCRONIZADO_EL\":\"26-02-2015 14:30:58\",\"CREADO_POR\":\"1481\"},"+
                "{\"ENVIO_HH\":\"1022131\",\"CORRELATIVO\":\"12\",\"NUMTAG\":\"18213\",\"GARRADAS\":\"8\",\"TIPO_TRABAJADOR\":\"R\",\"NUMTRC\":\"0\","+
                "\"SINCRONIZADO_EL\":\"26-02-2015 14:30:58\",\"CREADO_POR\":\"1481\"}] ^FS" +
                "^FO050,250^ADN,11,7^FD20173 11F 21^FS" +
                "^FO250,250^ADN,11,7^FD18758 11G 4^FS" +
                "^FO450,250^ADN,11,7^FD18208 10G 6 ^FS" +
                "^FO050,280^ADN,11,7^FD18213 8G 8^FS" +
                "^FO250,280^ADN,11,7^FD18213 9G 8^FS" +
                "^XZ" ;

         */

        AsyncEscPosPrinter printer = new AsyncEscPosPrinter(printerConnection, 203, 104f, 80);
        return printer.setTextToPrint( textToPrint ) ;
    }


}