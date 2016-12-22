package android.project;


import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;

public class FileSaveDialog extends Dialog {

    // File kinds - these should correspond to the order in which
    // they're presented in the spinner control
    public static final int FILE_KIND_MUSIC = 0;
    public static final int FILE_KIND_ALARM = 1;
    public static final int FILE_KIND_NOTIFICATION = 2;
    public static final int FILE_KIND_RINGTONE = 3;

    private Spinner mTypeSpinner;
    private EditText mFilename;
    private Message mResponse;
    private String mOriginalName;
    private ArrayList<String> mTypeArray;
    private int mPreviousSelection;

    /**
     * Return a human-readable name for a kind (music, alarm, ringtone, ...).
     * These won't be displayed on-screen (just in logs) so they shouldn't
     * be translated.
     */
    public static String KindToName(int kind) {
        switch(kind) {
            default:
                return "Unknown";
            case FILE_KIND_MUSIC:
                return "Music";
            case FILE_KIND_ALARM:
                return "Alarm";
            case FILE_KIND_NOTIFICATION:
                return "Notification";
            case FILE_KIND_RINGTONE:
                return "Ringtone";
        }
    }

    public FileSaveDialog(Context context,
                          Resources resources,
                          String originalName,
                          Message response) {
        super(context);

        // Inflate our UI from its XML layout description.
        setContentView(R.layout.ring_save);

        Button save = (Button)findViewById(R.id.save);
        final Button save_and_set = (Button)findViewById(R.id.save_and_set);
        Button cancel = (Button)findViewById(R.id.cancel);

        setTitle("保存为：");

        mTypeArray = new ArrayList<String>();
        mTypeArray.add("音乐");
        mTypeArray.add("闹钟铃声");
        mTypeArray.add("通知铃声");
        mTypeArray.add("铃声");

        mFilename = (EditText)findViewById(R.id.filename);
        mOriginalName = originalName;

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                context, android.R.layout.simple_spinner_item, mTypeArray);
        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);
        mTypeSpinner = (Spinner) findViewById(R.id.ringtone_type);
        mTypeSpinner.setAdapter(adapter);
        mTypeSpinner.setSelection(FILE_KIND_RINGTONE);
        mPreviousSelection = FILE_KIND_RINGTONE;

        setFilenameEditBoxFromName(false);

        mTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent,
                                       View v,
                                       int position,
                                       long id) {
                setFilenameEditBoxFromName(true);
                Log.d("position in dialog", ""+position);

                if(position==0){
                    //选择文件类型为音乐
                    save_and_set.setText("保存并分享");
                }else if(position==1){
                    //选择类型为闹钟铃声
                    save_and_set.setText("保存并设为闹钟铃声");
                }else if(position==2){
                    //选择类型为通知铃声
                    save_and_set.setText("保存并设为通知铃声");
                }else if(position==3){
                    save_and_set.setText("保存并设为铃声");
                }
            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        save.setOnClickListener(saveListener);
        save_and_set.setOnClickListener(saveandsetListener);
        cancel.setOnClickListener(cancelListener);
        mResponse = response;
    }

    private void setFilenameEditBoxFromName(boolean onlyIfNotEdited) {
        if (onlyIfNotEdited) {
            CharSequence currentText = mFilename.getText();
            String expectedText = mOriginalName + " " +
                    mTypeArray.get(mPreviousSelection);

            if (!expectedText.contentEquals(currentText)) {
                return;
            }
        }

        int newSelection = mTypeSpinner.getSelectedItemPosition();
        String newSuffix = mTypeArray.get(newSelection);
        mFilename.setText(mOriginalName + " " + newSuffix);
        mPreviousSelection = mTypeSpinner.getSelectedItemPosition();
    }

    private View.OnClickListener saveListener = new View.OnClickListener() {
        public void onClick(View view) {
            mResponse.obj = mFilename.getText();
            mResponse.arg1 = mTypeSpinner.getSelectedItemPosition();
            mResponse.arg2 = 0;
            mResponse.sendToTarget();
            dismiss();
        }
    };

    private View.OnClickListener saveandsetListener = new View.OnClickListener() {
        public void onClick(View view) {
            mResponse.obj = mFilename.getText();
            mResponse.arg1 = mTypeSpinner.getSelectedItemPosition();
            mResponse.arg2 = 1;
            mResponse.sendToTarget();
            dismiss();
        }
    };

    private View.OnClickListener cancelListener = new View.OnClickListener() {
        public void onClick(View view) {
            dismiss();
        }
    };
}
