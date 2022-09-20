package cn.gd.snm.testjetpact.utils;

import android.util.Log;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;

public class CustomLogInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        long startNs = System.nanoTime();//请求发起的时间
        Response response = chain.proceed(request);
        long tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs);
        printInfo(request, response, tookMs);
        return response;
    }

    private void printInfo(Request request, Response response,long tookMs) {
        StringBuilder mMessage = new StringBuilder();
        if (request != null && response != null) {
//            RequestBody requestBody = request.body();
            mMessage.append(":\n ");
            mMessage.append("===============Http Log===============\n");
            mMessage.append("Request Url-->：" + request.method() + " " + request.url().toString() + "\n");
            mMessage.append("Request Header-->：" + getRequestHeaders(request) + "\n");
            mMessage.append("Request Parameters-->：" + getRequestParams(request) + "\n");
            mMessage.append("Request Result-->：" + getResponseText(response,tookMs) + "\n");
            mMessage.append("======================================\n");

            Log.d("HttpLog", mMessage.toString().trim());
        }
    }

    private String getResponseText(Response response,long tookMs) {
        StringBuilder mMessage = new StringBuilder();
        mMessage.append("("+response.code() + (response.message().isEmpty() ? "" : ' ' + response.message()) + " "+ tookMs +"ms)");
        return mMessage.toString();
    }

    private String getRequestParams(Request request) {

        String str = "Empty!";
        try {
            RequestBody body = request.body();
            if (body != null) {
                Buffer buffer = new Buffer();
                body.writeTo(buffer);
                MediaType mediaType = body.contentType();
                if (mediaType != null) {
                    @SuppressWarnings("CharsetObjectCanBeUsed") Charset charset = mediaType.charset(
                            Charset.forName("UTF-8"));
                    if (charset != null) {

                        str = buffer.readString(charset);
                    }
                }
            }
        } catch (Exception e) {

            e.printStackTrace();
        }
        return str;
    }

    private String getRequestHeaders(Request request) {
        Headers headers = request.headers();
        if (headers.size() > 0) {
            return headers.toString();
        } else {
            return "Empty!";
        }
    }
}
