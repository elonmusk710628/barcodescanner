package ml.guru.barcodescanner;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import androidx.preference.PreferenceManager;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

public class Preference {

    private final SharedPreferences 	settings;
    private final Context 			mContext;

    public Preference (Context context)
    {
        this.settings = PreferenceManager.getDefaultSharedPreferences(context);
        this.mContext = context;

    }

    /** remove all settings */
    public void clear()
    {
        Editor editor = settings.edit();
        editor.clear();
        editor.commit();
    }

    public void editSettings(String name, String param)
    {
        Editor editor = settings.edit();
        editor.putString(name, param);
        editor.commit();
    }

    public void editSettings(String name, long param)
    {
        Editor editor = settings.edit();
        editor.putLong(name, param);
        editor.commit();
    }

    public void editSettings(String name, Boolean param)
    {
        Editor editor = settings.edit();
        editor.putBoolean(name, param);
        editor.commit();
    }

    public void editSettings(String name, String[] param)
    {
        Editor editor = settings.edit();
        editor.putStringSet(name, new HashSet<>(Arrays.asList(param)));
        editor.commit();
    }

    public void setStringArray(String name, String[] param)
    {
        Editor editor = settings.edit();
        if (param == null)
        {
            editor.putInt(name + "_size", 0);
            editor.commit();
            return;
        }

        editor.putInt(name + "_size", param.length);

        for (int i = 0; i < param.length; i++) {
            editor.putString(name + "_value_" + Integer.toString(i), param[i]); // Save each element with a unique key
        }

        editor.commit();
    }

    public String[] getStringArray(String name)
    {
        int arraySize = settings.getInt(name + "_size", 0); // Retrieve the array size

        if (arraySize == 0)
            return null;

        String[] stringArray = new String[arraySize];

        for (int i = 0; i < arraySize; i++) {
            stringArray[i] = settings.getString(name + "_value_" + Integer.toString(i), null); // Retrieve each element
        }

        return stringArray;
    }

    public String getCustomer(){
        return settings.getString("customer", "");
    }
    public void setCustomer(String strCustomer)
    {
        editSettings("customer", strCustomer);
    }
    public String getUserFullname(){
        return settings.getString("UserFullname", "");
    }
    public void setUserFullname(String strUserFullname)
    {
        editSettings("UserFullname", strUserFullname);
    }
    public String getUserCompany(){
        return settings.getString("UserCompany", "");
    }
    public void setUserCompany(String strUserCompany)
    {
        editSettings("UserCompany", strUserCompany);
    }
    public String getUserEmail(){
        return settings.getString("UserEmail", "");
    }
    public void setUserEmail(String strUserEmail)
    {
        editSettings("UserEmail", strUserEmail);
    }
    public String getUserCode(){
        return settings.getString("UserCode", "");
    }
    public void setUserCode(String strUserCode)
    {
        editSettings("UserCode", strUserCode);
    }
    public boolean getScanResultActivityCallFlag(){
        return settings.getBoolean("ScanResultActivity_Call_Flag", true);
    }
    public void setScanResultActivityCallFlag(boolean flag)
    {
        editSettings("ScanResultActivity_Call_Flag", flag);
    }
    public boolean getQtyFlag(){
        return settings.getBoolean("qty_flag", true);
    }
    public void setQtyFlag(boolean flag)
    {
        editSettings("qty_flag", flag);
    }
    public boolean getAcceptFlag(){
        return settings.getBoolean("qty_Accept", false);
    }
    public void setAcceptFlag(boolean flag)
    {
        editSettings("qty_Accept", flag);
    }
    public boolean getActivateFlag(){
        return settings.getBoolean("qty_Activate", false);
    }
    public void setActivateFlag(boolean flag)
    {
        editSettings("qty_Activate", flag);
    }
    public boolean getFiletypeFlag(){
        return settings.getBoolean("filetype_flag", true);
    }
    public void setFiletypeFlag(boolean flag)
    {
        editSettings("filetype_flag", flag);
    }
    public long getScans(){
        Long longValue = 0L;
        return settings.getLong("scans", longValue);
    }

    public void setScans(long nScans)
    {
        editSettings("scans", nScans);
    }
    public long getLastMonth(){
        Long longValue = 0L;
        return settings.getLong("last_month", longValue);
    }
    public void setLastMonth(long last_month)
    {
        editSettings("last_month", last_month);
    }
    public String getImageFilePath(){
        return settings.getString("imagefilepath", "");
    }
    public void setImageFilePath(String strImageFilePath)
    {
        editSettings("imagefilepath", strImageFilePath);
    }
    public String getEmailAddress(){
        return settings.getString("email_addr", "");
    }
    public void setEmailAddress(String strEmailAddress)
    {
        editSettings("email_addr", strEmailAddress);
    }
}
