package com.practikality.tarea;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TimePicker;

import java.util.Calendar;

public class add_task_activity extends AppCompatActivity implements View.OnClickListener{

    Button date_picker_button, time_picker_button, add_task_button;
    private int mYear, mMonth, mDay, mHour, mMinute;
    private RadioGroup radioGroup;
    private EditText from_edit_text, to_edit_text, title_edit_text, desc_edit_text;
    private String from_text, to_text, title_text, desc_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task_activity);

        date_picker_button =(Button)findViewById(R.id.date_picker_button);
        time_picker_button =(Button)findViewById(R.id.time_picker_button);
        add_task_button = (Button)findViewById(R.id.add_task_button);

        date_picker_button.setOnClickListener(this);
        time_picker_button.setOnClickListener(this);

        radioGroup = (RadioGroup) findViewById(R.id.priority_radio_group);
        radioGroup.clearCheck();
        RadioButton radioButton = (RadioButton) radioGroup.findViewById(radioGroup.getCheckedRadioButtonId());
        radioButton.getText(); //Priority chosen by the user

        from_edit_text = (EditText) findViewById(R.id.add_task_from_text);
        to_edit_text = (EditText) findViewById(R.id.add_task_to_text);
        title_edit_text = (EditText) findViewById(R.id.add_task_title_text);
        desc_edit_text = (EditText) findViewById(R.id.add_task_desc_text);
    }

    @Override
    public void onClick(View v) {   //Handling multiple click actions with one function

        if(v == date_picker_button){ //If Date Picker Button is clicked
            final Calendar calendar = Calendar.getInstance();
            mYear = calendar.get(Calendar.YEAR);
            mMonth = calendar.get(Calendar.MONTH);
            mDay = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    date_picker_button.setText(dayOfMonth + "/" +(month+1) + "/" + year);
                }
            }, mYear, mMonth, mDay);
            datePickerDialog.show();
        }

        if (v == time_picker_button){ //If Time Picker Button is clicked
            final Calendar calendar = Calendar.getInstance();
            mHour = calendar.get(Calendar.HOUR_OF_DAY);
            mMinute = calendar.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    time_picker_button.setText(hourOfDay + ":" + minute);
                }
            }, mHour, mMinute, false);
            timePickerDialog.show();
        }

        if(v == add_task_button){ //If Add Task Button is clicked
            from_text = from_edit_text.getText().toString();
            from_edit_text.setText("");
            to_text = to_edit_text.getText().toString();
            to_edit_text.setText("");
            title_text = title_edit_text.getText().toString();
            title_edit_text.setText("");
            desc_text = desc_edit_text.getText().toString();
            desc_edit_text.setText("");
        }
    }
}
