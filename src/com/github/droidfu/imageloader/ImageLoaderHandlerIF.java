package com.github.droidfu.imageloader;

import android.os.Handler;
import android.os.Message;

public interface ImageLoaderHandlerIF {

	public abstract void handleMessage(Message msg);

	public abstract Handler getHandler();

}