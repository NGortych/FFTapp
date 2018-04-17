package nikodem_gortych.fftapp;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import static java.lang.Math.pow;

/**
 * Created by user on 19.07.2017.
 */

public class OptionsActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private SeekBar seekBarSensorSpeed;
    private TextView seekBarSensorSpeedValue;
    private SeekBar seekBarTableSize;
    private TextView seekBarTableSizeValue;
    private TextView infTextView1;
    private TextView infTextView2;
    private TextView infTextView3;
    private Spinner spinner;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.options);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.settings));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitleTextColor(Color.WHITE);

        seekBarSensorSpeed = (SeekBar) findViewById(R.id.seekBarSensorSpeed);
        seekBarSensorSpeedValue = (TextView) findViewById(R.id.seekBarSensorSpeedValue);
        seekBarTableSize = (SeekBar) findViewById(R.id.seekBarTableSize);
        seekBarTableSizeValue = (TextView) findViewById(R.id.seekBarTableSizeValue);
        infTextView1 = (TextView) findViewById(R.id.infTextView);
        infTextView2 = (TextView) findViewById(R.id.infTextView2);
        infTextView3 = (TextView) findViewById(R.id.infTextView3);

        spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setSelection(Fragment_FFT.getCursor());

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if (Fragment_FFT.getCursor() != position) {
                    Fragment_FFT.setCursor(position);
                    Log.i("cos", "dsasaddsa");
                    if (position == 0) {
                        jTransformsSettings();
                    } else algs4Settings();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        getSettings();

        seekBarTableSize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (Fragment_FFT.getCursor() == 0) {
                    Fragment_FFT.setTableSize((seekBar.getProgress()));
                    setSeekBarTableSizeValue(seekBar.getProgress() + 8);
                    updateInformation();
                } else if (Fragment_FFT.getCursor() == 1) {
                    Fragment_FFT.setTableSize(seekBar.getProgress());
                    setSeekBarTableSizeValue((int) pow(2, seekBarTableSize.getProgress() + 3));
                    updateInformation();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        seekBarSensorSpeed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                MainActivity.setSensorSpeed(seekBar.getProgress());
                setSeekBarSensorSpeedValue(seekBar.getProgress());
                updateInformation();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void algs4Settings() {
        seekBarTableSize.setProgress(3);
        seekBarTableSize.setMax(4);
        Fragment_FFT.setTableSize(3);
        setSeekBarTableSizeValue(64);
    }

    private void jTransformsSettings() {
        seekBarTableSize.setMax(292);
        seekBarTableSize.setProgress(56);
        Fragment_FFT.setTableSize(64);
        setSeekBarTableSizeValue(64);
    }


    private void setSeekBarSensorSpeedValue(int i) {
        if (i == 0) {
            seekBarSensorSpeedValue.setText("200 ms");
        } else if (i == 1) {
            seekBarSensorSpeedValue.setText("60 ms");
        } else if (i == 2) {
            seekBarSensorSpeedValue.setText("20 ms");
        } else if (i == 3) {
            seekBarSensorSpeedValue.setText("0 ms");
        }

    }

    private void setSeekBarTableSizeValue(int i) {
        seekBarTableSizeValue.setText(String.valueOf(i));
    }

    private double realSensorSpeed(int i) {
        double j = 0;
        if (i == 0) {
            j = 0.2;
        } else if (i == 1) {
            j = 0.06;
        } else if (i == 2) {
            j = 0.02;
        } else if (i == 3) {
            j = 0.001;
        }
        return j;
    }

    private void updateInformation() {
        if (Fragment_FFT.getCursor() == 0)
            infTextView1.setText(getResources().getString(R.string.inf_part_1) + "  " + (double) Math.round((seekBarTableSize.getProgress() + 8) * realSensorSpeed(seekBarSensorSpeed.getProgress()) * 10d) / 10d);
        else if (Fragment_FFT.getCursor() == 1)
            infTextView1.setText(getResources().getString(R.string.inf_part_1) + "  " + (double) Math.round((pow(2, seekBarTableSize.getProgress() + 3)) * realSensorSpeed(seekBarSensorSpeed.getProgress()) * 10d) / 10d);
        infTextView2.setText(getResources().getString(R.string.inf_part_2) + "  " + (double) Math.round(1 / realSensorSpeed(seekBarSensorSpeed.getProgress()) * 10d) / 10d);
        infTextView3.setText(getResources().getString(R.string.inf_part_3));
    }

    private void getSettings() {

        seekBarSensorSpeed.setProgress(MainActivity.sensorSpeed_index);
        setSeekBarSensorSpeedValue(seekBarSensorSpeed.getProgress());
        if (Fragment_FFT.getCursor() == 0) {
            seekBarTableSize.setMax(292);
            seekBarTableSize.setProgress(((Fragment_FFT.tableSizeIndex)));
            setSeekBarTableSizeValue(seekBarTableSize.getProgress() + 8);
        } else if (Fragment_FFT.getCursor() == 1) {
            seekBarTableSize.setMax(4);
            seekBarTableSize.setProgress(((Fragment_FFT.tableSizeIndex)));
            setSeekBarTableSizeValue((int) pow(2, seekBarTableSize.getProgress() + 3));
        }
        updateInformation();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
