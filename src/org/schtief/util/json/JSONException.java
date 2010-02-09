package org.schtief.util.json;

import android.util.Log;

/**
 * The JSONException is thrown by the JSON.org classes then things are amiss.
 * enhanced by schtief for JSONable Service
 * 
 * @author Stefan Lischke
 * @version 2009-11-17
 */
public class JSONException extends Exception/* implements JSONable*/
{

//	private static Logger		LOG							= Logger.getLogger(JSONServiceInvocationHandler.class);

	public static final int	JSON_SYSTEM_EXCEPTION	= 1;

	private String					name						= null;
	private String					message					= null;
	private int	errorCode										=	JSON_SYSTEM_EXCEPTION;
	private Exception		cause						= null;


	/**
	 * for reflection only.
	 */
	public JSONException()
	{
		super();
	}

	protected JSONException(String message)
	{
		super();
		this.message = message;
		this.name	=	this.getClass().getName();
	}

	public JSONException(String message, int ec)
	{
		this(message);
		this.errorCode	=	ec;
	}


	public JSONException(Throwable t)
	{
		this(t.getMessage());
		this.name = t.getClass().getName();
		if (null != t.getCause())
			cause = new JSONException(t.getCause());
	}


	public JSONException(String message, int ec, Throwable c)
	{
		this(message,ec);
		if (null != c)
			cause = new JSONException(c);
	}


//	public JSONException(JSONObject jsonObject)
//	{
//		fromJSON(jsonObject);
//	}

	public JSONObject toJSON()
	{
		JSONObject o = new JSONObject();
		JSONArray properties = new JSONArray();
		properties.put(message);
		properties.put(errorCode);
		try
		{
			if (null != cause)
			{
				if(cause instanceof JSONException)
					properties.put(((JSONException)cause).toJSON());
				else
				{
					JSONObject co = new JSONObject();
					JSONArray cproperties = new JSONArray();
					cproperties.put(cause.getMessage());
					cproperties.put(-1);
					co.put(cause.getClass().getName(), properties);
					properties.put(co);
				}
			}
			o.put(name, properties);
		}
		catch (JSONException e)
		{
			try
			{
				return new JSONObject("{JSONException:[\"Konnte Exception nicht nach JSON schreiben '" + message + "'\"]}");
			}
			catch (JSONException e1)
			{
				Log.e("json","Konnte Exception nicht nach JSON schreiben '" + message + "'",e);
			}
		}
		return o;
	}


	@Override
	public Throwable getCause()
	{
		return cause;
	}


	@Override
	public String getMessage()
	{
		return this.message;
	}


	public int getErrorCode()
	{
		return errorCode;
	}

//	@Override
//	public void fromJSON(JSONObject jsonObject)
//	{
//		try
//		{
//			String[] name	=	JSONObject.getNames(jsonObject);
//			JSONArray properties	= jsonObject.getJSONArray(name[0]);
//			this.message	=	properties.getString(0);
//			this.errorCode	=	properties.getInt(1);
//			if(properties.length()==3)
//			{
//				this.cause	=	instantiateException(properties.getJSONObject(2));
//			}
//		}
//		catch(JSONException e)
//		{
//			throw new RuntimeException("Could not parse JSONException",e);//TODO schtief keine runtimeException bitte
//		}
//	}
//
//	public static Exception instantiateException(JSONObject jsonObject)
//	{
//		String[] name	=	JSONObject.getNames(jsonObject);
//		if(!name[0].contains("Exception"))
//			return null;
//		
//		//try to instantiate
//		Class exceptionClass;
//		Object exceptionO	=	null;
//		try
//		{
//			exceptionClass = Class.forName(name[0]);
//			exceptionO	=	exceptionClass.newInstance();
//		}
//		catch (Exception e)
//		{
//			LOG.error("Could not instantiate Exception: "+name[0],e);
//			return new JSONException(jsonObject);
//		}
//		
//		//test if Exception
//		if(!(exceptionO instanceof JSONException))
//			return (Exception)exceptionO;
//			//	throw new RuntimeException(name[0]+" is no JSONException");//TODO schtief keine runtimeException bitte
//		
//		JSONException exception 	=	(JSONException)exceptionO;
//		//if jsonable
//		exception.fromJSON(jsonObject);
//		return exception;
//	}
}
