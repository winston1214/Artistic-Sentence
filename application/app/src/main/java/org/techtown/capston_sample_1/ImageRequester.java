package org.techtown.capston_sample_1;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.SystemClock;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ImageRequester {
    final String serverIp;
    final int serverPort;

    private Socket clientSocket;
    private PrintWriter pw;
    private InputStream is;
    private BufferedInputStream bis;
    private DataInputStream dis;

    private Disposable disposable;
    private ImageCallback imageCallback;

    private ResultData resultData;

    public ImageRequester(String serverIp, int serverPort, ImageCallback imageCallback) {
        this.serverIp = serverIp;
        this.serverPort = serverPort;
        this.imageCallback = imageCallback;
    }

    public void requestImage(String b, String translatedInput, String styleInput, String quality) {
        execute(b, translatedInput, styleInput, quality);
    }

    private void execute(String b, String translatedInput, String styleInput, String quality) {
        disposable = Observable.fromCallable(() -> {
            try {
                clientSocket = new Socket();
                clientSocket.connect(new InetSocketAddress(serverIp, serverPort), 5 * 1000);

                // 데이터 전송
                pw = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream(), "UTF-8"));
                pw.write(b + "|" + translatedInput + "|" + styleInput + "|" + quality);
                pw.flush();

                // 데이터 수신
                is = clientSocket.getInputStream();
                bis = new BufferedInputStream(is);
                dis = new DataInputStream(bis);

                byte[] textLengthBytes = new byte[4];
                is.read(textLengthBytes, 0, 4);
                ByteBuffer byteBuffer = ByteBuffer.wrap(textLengthBytes);
                byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
                int textLength = byteBuffer.getInt();
                Log.d("<<ImageRequester>>", String.valueOf(textLength));

                byte[] imageLengthBytes = new byte[4];
                is.read(imageLengthBytes, 0, 4);
                ByteBuffer byteBuffer1 = ByteBuffer.wrap(imageLengthBytes);
                byteBuffer1.order(ByteOrder.LITTLE_ENDIAN);
                int imageLength = byteBuffer1.getInt();
                Log.d("<<ImageRequester>>", String.valueOf(imageLength));

                byte[] textData = new byte[textLength];
                is.read(textData, 0, textLength);
                String text = new String(textData, StandardCharsets.UTF_8);
                Log.d("<<ImageRequester>>", text);

                byte[] imageData = new byte[imageLength];
                dis.readFully(imageData, 0, imageLength);
                Bitmap bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);

                // resultData 에 담기
                resultData = new ResultData();
                resultData.setText(text);
                resultData.setBitmap(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if(pw != null)
                        pw.close();
                    if(is != null)
                        is.close();
                    if(bis != null)
                        bis.close();
                    if(dis != null)
                        dis.close();
                    if(clientSocket != null)
                        clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return resultData;
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<ResultData>() {
            @Override
            public void accept(ResultData resultData) throws Throwable {
                imageCallback.onResult(resultData);
            }
        });
    }

}
