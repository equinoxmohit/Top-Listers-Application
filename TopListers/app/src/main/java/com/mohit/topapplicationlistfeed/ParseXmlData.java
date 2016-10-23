package com.mohit.topapplicationlistfeed;


import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;



public class ParseXmlData {
    private static final String TAG = "ParseXmlData";
    private ArrayList<FeedEntry> applications;

    public ParseXmlData()
    {
        this.applications=new ArrayList<>();
    }

    public ArrayList<FeedEntry> getApplications() {
        return applications;
    }

    public boolean parse(String xmlData)
    {
        boolean status=true;
        boolean inEntry=false;
        FeedEntry currentRecord=null;
        String textContent="";
        try
        {
            XmlPullParserFactory factory=XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp=factory.newPullParser();
            xpp.setInput(new StringReader(xmlData));
            int eventType=xpp.getEventType();
            while(eventType!=XmlPullParser.END_DOCUMENT)
            {
                String tagName=xpp.getName();
                switch (eventType)
                {
                    case XmlPullParser.START_TAG:
                        if("entry".equalsIgnoreCase(tagName))
                        {
                            currentRecord=new FeedEntry();
                            inEntry=true;
                        }
                        break;
                    case XmlPullParser.TEXT:
                        textContent=xpp.getText();
                        break;
                    case XmlPullParser.END_TAG:
                        if(inEntry)
                        {
                            if("entry".equalsIgnoreCase(tagName))
                            {
                                applications.add(currentRecord);
                                inEntry=false;
                            }
                            else if("name".equalsIgnoreCase(tagName))
                            {
                                currentRecord.setName(textContent);
                            }else if("artist".equalsIgnoreCase(tagName))
                            {
                                currentRecord.setArtist(textContent);
                            }else if("summary".equalsIgnoreCase(tagName))
                            {
                                currentRecord.setSummary(textContent);
                            }else if("releaseDate".equalsIgnoreCase(tagName))
                            {
                                currentRecord.setReleaseDate(textContent);
                            }else if("image".equalsIgnoreCase(tagName))
                            {
                                currentRecord.setImageUrl(textContent);
                            }
                        }
                        break;

                }
                eventType=xpp.next();

            }

            /* for(FeedEntry e:applications)
            {
                Log.d(TAG, "parse:****************************");
                Log.d(TAG, e.toString());
            }*/

        }catch(XmlPullParserException|IOException ex)
        {
                status=false;
            Log.d(TAG, "parse: error"+ex.getLocalizedMessage());
        }
        return status;
    }
}
