package cs407.socialkarmaapp;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import cs407.socialkarmaapp.Helpers.APIClient;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AddMeetupActivity extends AppCompatActivity {
    private static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;

    private Toolbar toolbar;
    private EditText titleEditText;
    private EditText descriptionEditText;
    private EditText startDateEditText;
    private EditText startTimeEditText;
    private EditText endDateEditText;
    private EditText endTimeEditText;
    private TextView meetupLocationTextView;
    private Button locationButton;
    private Button submitButton;
    private Place selectedLocation;

    private Calendar calendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_meetup);

        setupToolbar();
        setupViews();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                selectedLocation = place;
                meetupLocationTextView.setText(place.getName());
                meetupLocationTextView.setVisibility(View.VISIBLE);
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                Toast.makeText(AddMeetupActivity.this, "Failed to get location. Please try again later.", Toast.LENGTH_SHORT).show();

            } else if (resultCode == RESULT_CANCELED) {
            }
        }
    }

    private void setupToolbar() {
        toolbar = findViewById(R.id.toolbar_add_meetup);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void setupViews() {
        titleEditText = findViewById(R.id.editText_title);
        descriptionEditText = findViewById(R.id.editText_meetup_description);
        startDateEditText = findViewById(R.id.editText_startTime_date);
        startTimeEditText = findViewById(R.id.editText_startTime);
        endDateEditText = findViewById(R.id.editText_endTime_date);
        endTimeEditText = findViewById(R.id.editText_endTime);
        meetupLocationTextView = findViewById(R.id.textView_meetup_location);
        locationButton = findViewById(R.id.button_location_meetup);
        submitButton = findViewById(R.id.button_submit_meetup);

        final DatePickerDialog.OnDateSetListener startDate = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateStartDate();
            }
        };


        final DatePickerDialog.OnDateSetListener endDate = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateEndDate();
            }
        };

        final TimePickerDialog.OnTimeSetListener startTime = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                startTimeEditText.setText(String.format("%02d", hourOfDay) + ":" + String.format("%02d", minute));
            }
        };

        final TimePickerDialog.OnTimeSetListener endTime = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                endTimeEditText.setText(String.format("%02d", hourOfDay) + ":" + String.format("%02d", minute));
            }
        };

        startDateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(AddMeetupActivity.this, startDate, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        endDateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(AddMeetupActivity.this, endDate, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        startTimeEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(AddMeetupActivity.this, startTime, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
            }
        });

        endTimeEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(AddMeetupActivity.this, endTime, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
            }
        });

        final AddMeetupActivity context = this;
        locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent =
                            new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                                    .build(context);
                    startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
                } catch (GooglePlayServicesRepairableException e) {
                    Toast.makeText(context, "Failed to get location. Please try again later.", Toast.LENGTH_SHORT).show();
                } catch (GooglePlayServicesNotAvailableException e) {
                    Toast.makeText(context, "Failed to get location. Please try again later.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (titleEditText.getText() == null || titleEditText.getText().length() == 0) {
                    Toast.makeText(context, "Failed to obtain a title. Please try again.", Toast.LENGTH_SHORT).show();
                    return;
                }

                String startTimeString = startDateEditText.getText() + "T" + startTimeEditText.getText();
                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy'T'hh:mm");
                long epochStart = 0;
                try {
                    Date gmt = sdf.parse(startTimeString);
                    epochStart = gmt.getTime() / 1000;
                } catch(Exception e) {
                    Toast.makeText(context, "Failed to obtain a start time. Please try again.", Toast.LENGTH_SHORT).show();
                    return;
                }

                String endTimeString = endDateEditText.getText() + "T" + endTimeEditText.getText();
                long epochEnd = 0;
                try {
                    Date gmt = sdf.parse(endTimeString);
                    epochEnd = gmt.getTime() / 1000;
                } catch(Exception e) {
                    Toast.makeText(context, "Failed to obtain an end time. Please try again.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (epochStart > epochEnd) {
                    Toast.makeText(context, "Meetup ends before it starts. Please try again.", Toast.LENGTH_SHORT).show();
                    return;
                } else if (selectedLocation == null) {
                    Toast.makeText(context, "Failed to obtain a location. Please try again.", Toast.LENGTH_SHORT).show();
                    return;
                }

                APIClient.INSTANCE.postNewMeetup(titleEditText.getText().toString(), descriptionEditText.getText().toString(), epochStart, epochEnd, selectedLocation, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(context, "Failed to create a meetup. Please try again later.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.code() >= 400) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(context, "Failed to create a meetup. Please try again later.", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            finish();
                        }
                    }
                });
            }
        });
    }

    private void updateStartDate() {
        String dateFormat = "MM/dd/yy";
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.US);
        startDateEditText.setText(sdf.format(calendar.getTime()));
    }

    private void updateEndDate() {
        String dateFormat = "MM/dd/yy";
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.US);
        endDateEditText.setText(sdf.format(calendar.getTime()));
    }
}
