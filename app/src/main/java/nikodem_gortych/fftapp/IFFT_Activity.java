package nikodem_gortych.fftapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import org.jtransforms.fft.DoubleFFT_1D;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import edu.princeton.cs.algs4.Complex;

import static edu.princeton.cs.algs4.FFT.fft;
import static edu.princeton.cs.algs4.FFT.ifft;

/**
 * Created by user on 25.07.2017.
 */

public class IFFT_Activity extends AppCompatActivity {

    private Toolbar toolbar;
    private RelativeLayout x_axis_container;
    private RelativeLayout y_axis_container;
    private RelativeLayout z_axis_container;
    private ImageButton saveButton;

    public int cursor, tableSize;

    public static List<Float> x_value_list = new ArrayList<>();
    public static List<Float> y_value_list = new ArrayList<>();
    public static List<Float> z_value_list = new ArrayList<>();

    public static List<Float> x_IFFT_value_list = new ArrayList<>();
    public static List<Float> y_IFFT_value_list = new ArrayList<>();
    public static List<Float> z_IFFT_value_list = new ArrayList<>();

    int loop = 0;
    int plot_iterator = 0;
    int index = 0;

    private GraphicalView x_mChart;
    private GraphicalView y_mChart;
    private GraphicalView z_mChart;
    private XYSeries xSeries;
    private XYSeries ySeries;
    private XYSeries zSeries;
    private XYSeries xSeries_IFFT;
    private XYSeries ySeries_IFFT;
    private XYSeries zSeries_IFFT;
    private XYMultipleSeriesDataset dataSet_X;
    private XYMultipleSeriesDataset dataSet_Y;
    private XYMultipleSeriesDataset dataSet_Z;
    private XYMultipleSeriesRenderer x_multiRenderer;
    private XYMultipleSeriesRenderer y_multiRenderer;
    private XYMultipleSeriesRenderer z_multiRenderer;

    private XYSeriesRenderer SeriesRenderer;
    private XYSeriesRenderer IFFT_SeriesRenderer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ifft_activity);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.ifft_page));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitleTextColor(Color.WHITE);

        x_axis_container = (RelativeLayout) findViewById(R.id.x_axis_container);
        y_axis_container = (RelativeLayout) findViewById(R.id.y_axis_container);
        z_axis_container = (RelativeLayout) findViewById(R.id.z_axis_container);
        saveButton = (ImageButton) findViewById(R.id.saveButton);
        saveButton.setVisibility(View.VISIBLE);

        setupChart();
        cursor = Fragment_FFT.getCursor();
        tableSize = Fragment_FFT.getTableSize();

        IFFT_Calculation ifft_cal = new IFFT_Calculation(IFFT_Activity.this);
        ifft_cal.execute();

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final AlertDialog alertSave = new AlertDialog.Builder(IFFT_Activity.this).create();
                LayoutInflater inflater = IFFT_Activity.this.getLayoutInflater();
                View layout = inflater.inflate(R.layout.save_dialog, null);
                final EditText editText2 = (EditText) layout.findViewById(R.id.editText2);
                alertSave.setView(layout);
                alertSave.show();

                Button buttonSave = (Button) layout.findViewById(R.id.saveFileButton);
                buttonSave.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        String fileName = editText2.getText().toString().trim();

                        if (fileName.length() != 0) {
                            fileName = fileName + ".txt";
                            String path =
                                    Environment.getExternalStorageDirectory() + File.separator + getResources().getString(R.string.folder_name);
                            File folder = new File(path);
                            folder.mkdirs();
                            File file = new File(folder, fileName);
                            try {
                                file.createNewFile();
                                FileOutputStream fOut = new FileOutputStream(file);
                                OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);

                                myOutWriter.append(getResources().getString(R.string.break_in_file_1));
                                for (int i = 0; i < x_value_list.size(); i++) {
                                    myOutWriter.append(
                                            String.valueOf((double) Math.round(x_value_list.get(i) * 1000000d) / 1000000d) + "\t\t" +
                                                    String.valueOf((double) Math.round(x_IFFT_value_list.get(i) * 1000000d) / 1000000d) + "\t\t" +
                                                    String.valueOf(Math.abs((double) Math.round((x_value_list.get(i) - x_IFFT_value_list.get(i)) * 1000000d) / 1000000d)) + "\n");
                                }

                                myOutWriter.append(getResources().getString(R.string.break_in_file_2));
                                for (int i = 0; i < y_value_list.size(); i++) {
                                    myOutWriter.append(
                                            String.valueOf((double) Math.round(y_value_list.get(i) * 1000000d) / 1000000d) + "\t\t" +
                                                    String.valueOf((double) Math.round(y_IFFT_value_list.get(i) * 1000000d) / 1000000d) + "\t\t" +
                                                    String.valueOf(Math.abs((double) Math.round((y_value_list.get(i) - y_IFFT_value_list.get(i)) * 1000000d) / 1000000d)) + "\n");
                                }

                                myOutWriter.append(getResources().getString(R.string.break_in_file_3));
                                for (int i = 0; i < z_value_list.size(); i++) {
                                    myOutWriter.append(
                                            String.valueOf((double) Math.round(z_value_list.get(i) * 1000000d) / 1000000d) + "\t\t" +
                                                    String.valueOf((double) Math.round(z_IFFT_value_list.get(i) * 1000000d) / 1000000d) + "\t\t" +
                                                    String.valueOf(Math.abs((double) Math.round((z_value_list.get(i) - z_IFFT_value_list.get(i)) * 1000000d) / 1000000d)) + "\n");
                                }

                                myOutWriter.close();
                                fOut.flush();
                                fOut.close();
                                Toast.makeText(IFFT_Activity.this, getResources().getString(R.string.file_saved), Toast.LENGTH_LONG).show();
                                alertSave.dismiss();

                            } catch (IOException e) {

                            }
                        } else
                            Toast.makeText(IFFT_Activity.this, getResources().getString(R.string.no_file), Toast.LENGTH_LONG).show();
                    }
                });

                Button buttonClose = (Button) layout.findViewById(R.id.closeButton);
                buttonClose.setOnClickListener(new View.OnClickListener() {


                    @Override
                    public void onClick(View view) {
                        alertSave.dismiss();
                    }
                });


            }
        });
    }

    public void clearData() {
        x_value_list.clear();
        y_value_list.clear();
        z_value_list.clear();
        x_IFFT_value_list.clear();
        y_IFFT_value_list.clear();
        z_IFFT_value_list.clear();

        loop = 0;
        plot_iterator = 0;
        index = 0;
    }

    private void addDataToChart() {
        Log.i("TAGaaaa", "no co posdsadsasadzlo nie tka");
        for (int i = 0; i < x_value_list.size(); i++) {
            xSeries.add(i, x_value_list.get(i));
            ySeries.add(i, y_value_list.get(i));
            zSeries.add(i, z_value_list.get(i));
        }

        for (int i = 0; i < x_IFFT_value_list.size(); i++) {
            xSeries_IFFT.add(i, x_IFFT_value_list.get(i));
            ySeries_IFFT.add(i, y_IFFT_value_list.get(i));
            zSeries_IFFT.add(i, z_IFFT_value_list.get(i));
        }


    }

    private void updateChart() {
        if (x_IFFT_value_list.size() < tableSize) {
            x_multiRenderer.setXAxisMin(0);
            x_multiRenderer.setXAxisMax(x_IFFT_value_list.size());
            y_multiRenderer.setXAxisMin(0);
            y_multiRenderer.setXAxisMax(x_IFFT_value_list.size());
            z_multiRenderer.setXAxisMin(0);
            z_multiRenderer.setXAxisMax(x_IFFT_value_list.size());
        } else {
            x_multiRenderer.setXAxisMax(x_IFFT_value_list.size());
            x_multiRenderer.setXAxisMin(x_IFFT_value_list.size() - tableSize);
            y_multiRenderer.setXAxisMax(x_IFFT_value_list.size());
            y_multiRenderer.setXAxisMin(x_IFFT_value_list.size() - tableSize);
            z_multiRenderer.setXAxisMax(x_IFFT_value_list.size());
            z_multiRenderer.setXAxisMin(x_IFFT_value_list.size() - tableSize);
        }
        x_mChart.repaint();
        y_mChart.repaint();
        z_mChart.repaint();
    }

    public void updateData(float x, float y, float z) {
        x_value_list.add(x);
        y_value_list.add(y);
        z_value_list.add(z);
    }

    private void setupChart() {
        //tworzymy obiekty serii danych
        xSeries = new XYSeries(getResources().getString(R.string.x_axis));
        xSeries_IFFT = new XYSeries(getResources().getString(R.string.x_axis));
        ySeries = new XYSeries(getResources().getString(R.string.y_axis));
        ySeries_IFFT = new XYSeries(getResources().getString(R.string.x_axis));
        zSeries = new XYSeries(getResources().getString(R.string.z_axis));
        zSeries_IFFT = new XYSeries(getResources().getString(R.string.x_axis));

        //tworzymy obiekt zbioru danych
        dataSet_X = new XYMultipleSeriesDataset();
        dataSet_Y = new XYMultipleSeriesDataset();
        dataSet_Z = new XYMultipleSeriesDataset();

        //i dodajemy do niego nasze serie danych
        dataSet_X.addSeries(xSeries);
        dataSet_X.addSeries(xSeries_IFFT);
        dataSet_Y.addSeries(ySeries);
        dataSet_Y.addSeries(ySeries_IFFT);
        dataSet_Z.addSeries(zSeries);
        dataSet_Z.addSeries(zSeries_IFFT);

        SeriesRenderer = new XYSeriesRenderer();
        SeriesRenderer.setColor(Color.GREEN);
        SeriesRenderer.setLineWidth(2);

        IFFT_SeriesRenderer = new XYSeriesRenderer();
        IFFT_SeriesRenderer.setColor(Color.RED);
        IFFT_SeriesRenderer.setLineWidth(2);

        x_multiRenderer = new XYMultipleSeriesRenderer();
        y_multiRenderer = new XYMultipleSeriesRenderer();
        z_multiRenderer = new XYMultipleSeriesRenderer();

        multiRendererSettings(x_multiRenderer);
        multiRendererSettings(y_multiRenderer);
        multiRendererSettings(z_multiRenderer);

        x_multiRenderer.addSeriesRenderer(SeriesRenderer);
        x_multiRenderer.addSeriesRenderer(IFFT_SeriesRenderer);
        y_multiRenderer.addSeriesRenderer(SeriesRenderer);
        y_multiRenderer.addSeriesRenderer(IFFT_SeriesRenderer);
        z_multiRenderer.addSeriesRenderer(SeriesRenderer);
        z_multiRenderer.addSeriesRenderer(IFFT_SeriesRenderer);
        x_multiRenderer.setChartTitle(getResources().getString(R.string.x_axis));
        y_multiRenderer.setChartTitle(getResources().getString(R.string.y_axis));
        z_multiRenderer.setChartTitle(getResources().getString(R.string.z_axis));

        x_mChart = (GraphicalView) ChartFactory.getLineChartView(this, dataSet_X, x_multiRenderer);
        y_mChart = (GraphicalView) ChartFactory.getLineChartView(this, dataSet_Y, y_multiRenderer);
        z_mChart = (GraphicalView) ChartFactory.getLineChartView(this, dataSet_Z, z_multiRenderer);
        x_axis_container.addView(x_mChart);
        y_axis_container.addView(y_mChart);
        z_axis_container.addView(z_mChart);

    }

    private void multiRendererSettings(XYMultipleSeriesRenderer multiRenderer) {
        multiRenderer.setXTitle(getResources().getString(R.string.iterations));
        multiRenderer.setYTitle(getResources().getString(R.string.acceleration));

        multiRenderer.setShowLegend(false);

        multiRenderer.setChartTitleTextSize(getResources().getDimension(R.dimen.chart_title_text_size));
        multiRenderer.setAxisTitleTextSize(getResources().getDimension(R.dimen.axis_title_text_size));
        multiRenderer.setLegendTextSize(getResources().getDimension(R.dimen.legend_text_size));
        multiRenderer.setLabelsTextSize(getResources().getDimension(R.dimen.labels_text_size));
        multiRenderer.setPointSize(5);

        multiRenderer.setMargins(new int[]{
                (int) getResources().getDimension(R.dimen.margin_top),
                (int) getResources().getDimension(R.dimen.margin_left),
                (int) getResources().getDimension(R.dimen.margin_bottom),
                (int) getResources().getDimension(R.dimen.margin_right)
        });

        multiRenderer.setBackgroundColor(Color.BLACK);
        multiRenderer.setApplyBackgroundColor(true);

        multiRenderer.setShowGrid(true);
        multiRenderer.setPanEnabled(true, false);
        multiRenderer.setExternalZoomEnabled(false);
        multiRenderer.setZoomEnabled(false, false);
    }

    private void clearIFFTList() {
        x_IFFT_value_list.clear();
        y_IFFT_value_list.clear();
        z_IFFT_value_list.clear();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            clearIFFTList();
            saveButton.setVisibility(View.INVISIBLE);
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class IFFT_Calculation extends AsyncTask<Void, Void, Void> {
        private Activity activity;
        ProgressDialog pdLoading;

        DoubleFFT_1D fftD_X = new DoubleFFT_1D(tableSize);
        DoubleFFT_1D fftD_Y = new DoubleFFT_1D(tableSize);
        DoubleFFT_1D fftD_Z = new DoubleFFT_1D(tableSize);

        double[] fftX = new double[tableSize * 2];
        double[] fftY = new double[tableSize * 2];
        double[] fftZ = new double[tableSize * 2];

        double[] ifft_x = new double[tableSize];
        double[] ifft_y = new double[tableSize];
        double[] ifft_z = new double[tableSize];

        double[] inputX = new double[tableSize];
        double[] inputY = new double[tableSize];
        double[] inputZ = new double[tableSize];

        Complex[] x_ifft;
        Complex[] y_ifft;
        Complex[] z_ifft;

        Complex[] x_Complex = new Complex[tableSize];
        Complex[] y_Complex = new Complex[tableSize];
        Complex[] z_Complex = new Complex[tableSize];

        Complex[] x_algs4;
        Complex[] y_algs4;
        Complex[] z_algs4;

        double freq;

        public IFFT_Calculation(Activity activity) {
            this.activity = activity;
            pdLoading = new ProgressDialog(activity);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //this method will be running on UI thread
            pdLoading = ProgressDialog.show(activity, getResources().getString(R.string.calculation_in_progress),
                    getResources().getString(R.string.please_wait), true);
            pdLoading.show();
        }

        @Override
        protected Void doInBackground(Void... params) {

            for (int i = 0; i < x_value_list.size(); i++) {

                if (loop == tableSize) {
                    loop = 0;
                }

                inputX[loop] = x_value_list.get(i);
                inputY[loop] = y_value_list.get(i);
                inputZ[loop] = z_value_list.get(i);

                if (plot_iterator >= tableSize) index++;
                if (index == tableSize) index = 0;

                System.arraycopy(inputX, index, fftX, 0, tableSize - index);
                System.arraycopy(inputX, 0, fftX, tableSize - index, index);
                System.arraycopy(inputY, index, fftY, 0, tableSize - index);
                System.arraycopy(inputY, 0, fftY, tableSize - index, index);
                System.arraycopy(inputZ, index, fftZ, 0, tableSize - index);
                System.arraycopy(inputZ, 0, fftZ, tableSize - index, index);

                if (cursor == 0) {
                    fftD_X.realForwardFull(fftX);
                    fftD_Y.realForwardFull(fftY);
                    fftD_Z.realForwardFull(fftZ);

                    for (int j = 0; j < tableSize; j++) {

                        freq = 50 * (double) j / (double) tableSize;
                        if (freq > 15 && freq < 35) {
                            fftX[2 * j] = 0.0;
                            fftX[2 * j + 1] = 0.0;
                            fftY[2 * j] = 0.0;
                            fftY[2 * j + 1] = 0.0;
                            fftZ[2 * j] = 0.0;
                            fftZ[2 * j + 1] = 0.0;
                        }
                    }

                    fftD_X.complexInverse(fftX, true);
                    fftD_Y.complexInverse(fftY, true);
                    fftD_Z.complexInverse(fftZ, true);

                    for (int j = 0; j < tableSize; j++) {
                        ifft_x[j] = fftX[j * 2];
                        ifft_y[j] = fftY[j * 2];
                        ifft_z[j] = fftZ[j * 2];
                    }

                    if (plot_iterator < tableSize) {
                        IFFT_Activity.x_IFFT_value_list.add((float) ifft_x[plot_iterator]);
                        IFFT_Activity.y_IFFT_value_list.add((float) ifft_y[plot_iterator]);
                        IFFT_Activity.z_IFFT_value_list.add((float) ifft_z[plot_iterator]);

                    } else {
                        IFFT_Activity.x_IFFT_value_list.add((float) ifft_x[tableSize - 1]);
                        IFFT_Activity.y_IFFT_value_list.add((float) ifft_y[tableSize - 1]);
                        IFFT_Activity.z_IFFT_value_list.add((float) ifft_z[tableSize - 1]);
                    }

                } else if (cursor == 1) {
                    for (int j = 0; j < tableSize; j++) {
                        x_Complex[j] = new Complex(fftX[j], 0);
                        y_Complex[j] = new Complex(fftY[j], 0);
                        z_Complex[j] = new Complex(fftZ[j], 0);
                    }

                    x_algs4 = fft(x_Complex);
                    y_algs4 = fft(y_Complex);
                    z_algs4 = fft(z_Complex);

                    for (int j = 0; j < tableSize; j++) {

                        freq = 50 * (double) j / (double) tableSize;
                        if (freq > 15 && freq < 35) {
                            x_algs4[j] = x_algs4[j].minus(x_algs4[j]);
                            y_algs4[j] = y_algs4[j].minus(y_algs4[j]);
                            z_algs4[j] = z_algs4[j].minus(z_algs4[j]);
                        }
                    }

                    x_ifft = ifft(x_algs4);
                    y_ifft = ifft(y_algs4);
                    z_ifft = ifft(z_algs4);

                    for (int j = 0; j < tableSize; j++) {
                        ifft_x[j] = x_ifft[j].re();
                        ifft_y[j] = y_ifft[j].re();
                        ifft_z[j] = z_ifft[j].re();
                    }

                    if (plot_iterator < tableSize) {
                        IFFT_Activity.x_IFFT_value_list.add((float) ifft_x[plot_iterator]);
                        IFFT_Activity.y_IFFT_value_list.add((float) ifft_y[plot_iterator]);
                        IFFT_Activity.z_IFFT_value_list.add((float) ifft_z[plot_iterator]);

                    } else {

                        IFFT_Activity.x_IFFT_value_list.add((float) ifft_x[tableSize - 1]);
                        IFFT_Activity.y_IFFT_value_list.add((float) ifft_y[tableSize - 1]);
                        IFFT_Activity.z_IFFT_value_list.add((float) ifft_z[tableSize - 1]);
                    }
                }

                loop++;
                plot_iterator++;
            }

            addDataToChart();
            updateChart();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            pdLoading.dismiss();
        }
    }
}
