package nikodem_gortych.fftapp;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import org.jtransforms.fft.DoubleFFT_1D;

import java.util.Arrays;

import edu.princeton.cs.algs4.Complex;

import static edu.princeton.cs.algs4.FFT.fft;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

public class Fragment_FFT extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    View view;
    LinearLayout chart_container;

    private static int cursor = 0;
    public static int tableSize = 64;
    public static int tableSizeIndex = 56;

    int loop = 0;
    int plot_iterator = 0;
    double freq;
    double re, im;

    static Double[] xNormalizacja = new Double[tableSize];
    static Double[] yNormalizacja = new Double[tableSize];
    static Double[] zNormalizacja = new Double[tableSize];

    static DoubleFFT_1D fftD_X = new DoubleFFT_1D(tableSize);
    static DoubleFFT_1D fftD_Y = new DoubleFFT_1D(tableSize);
    static DoubleFFT_1D fftD_Z = new DoubleFFT_1D(tableSize);

    static double[] fftX = new double[tableSize * 2];
    static double[] fftY = new double[tableSize * 2];
    static double[] fftZ = new double[tableSize * 2];

    static double[] inputX = new double[tableSize];
    static double[] inputY = new double[tableSize];
    static double[] inputZ = new double[tableSize];

    Complex[] x_algs4;
    Complex[] y_algs4;
    Complex[] z_algs4;

    static Complex[] x_Complex = new Complex[tableSize];
    static Complex[] y_Complex = new Complex[tableSize];
    static Complex[] z_Complex = new Complex[tableSize];

    int index = 0;
    double max;

    private CheckBox checkBox_XAxis;
    private CheckBox checkBox_YAxis;
    private CheckBox checkBox_ZAxis;

    private GraphicalView mChart;
    private XYSeries xSeries;
    private XYSeries ySeries;
    private XYSeries zSeries;
    private XYMultipleSeriesDataset dataSet;
    private XYSeriesRenderer xSeriesRenderer;
    private XYSeriesRenderer ySeriesRenderer;
    private XYSeriesRenderer zSeriesRenderer;
    private XYMultipleSeriesRenderer multiRenderer;

    public Fragment_FFT() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static Fragment_FFT newInstance(String param1, String param2) {
        Fragment_FFT fragment = new Fragment_FFT();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_fft, container, false);
        chart_container = (LinearLayout) view.findViewById(R.id.chart_container);

        checkBox_XAxis = (CheckBox) view.findViewById(R.id.x_checkBox);
        checkBox_YAxis = (CheckBox) view.findViewById(R.id.y_checkBox);
        checkBox_ZAxis = (CheckBox) view.findViewById(R.id.z_checkBox);

        setupChart();

        checkBox_XAxis.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (checkBox_XAxis.isChecked()) {
                    checkBox_XAxis.setChecked(true);
                    addXAxis();
                } else {
                    checkBox_XAxis.setChecked(false);
                    removeXAxis();
                }
            }
        });

        checkBox_YAxis.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (checkBox_YAxis.isChecked()) {
                    checkBox_YAxis.setChecked(true);
                    addYAxis();
                } else {
                    checkBox_YAxis.setChecked(false);
                    removeYAxis();
                }
            }
        });

        checkBox_ZAxis.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (checkBox_ZAxis.isChecked()) {
                    checkBox_ZAxis.setChecked(true);
                    addZAxis();
                } else {
                    checkBox_ZAxis.setChecked(false);
                    removeZAxis();
                }
            }
        });

        return view;
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }


    @Override
    public void onStart() {
        super.onStart();
        try {
            mListener = (OnFragmentInteractionListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public static int getCursor() {
        return cursor;
    }

    public static void setCursor(int cursor) {
        Fragment_FFT.cursor = cursor;
    }

    public static int getTableSize() {
        return tableSize;
    }

    public static void setTableSize(int i) {
        if (cursor == 0) {
            tableSizeIndex = i;
            tableSize = i + 8;
        } else if (cursor == 1) {
            tableSizeIndex = i;
            tableSize = (int) pow(2, 3 + i);
        }

        xNormalizacja = new Double[tableSize];
        yNormalizacja = new Double[tableSize];
        zNormalizacja = new Double[tableSize];

        fftD_X = new DoubleFFT_1D(tableSize);
        fftD_Y = new DoubleFFT_1D(tableSize);
        fftD_Z = new DoubleFFT_1D(tableSize);

        fftX = new double[tableSize * 2];
        fftY = new double[tableSize * 2];
        fftZ = new double[tableSize * 2];

        inputX = new double[tableSize];
        inputY = new double[tableSize];
        inputZ = new double[tableSize];

        x_Complex = new Complex[tableSize];
        y_Complex = new Complex[tableSize];
        z_Complex = new Complex[tableSize];
    }

    public void clearData() {
        Arrays.fill(inputX, 0);
        Arrays.fill(inputY, 0);
        Arrays.fill(inputZ, 0);

        loop = 0;
        plot_iterator = 0;
        index = 0;
    }

    public void readFromAccelerometer(float x, float y, float z) {


        if (loop == tableSize) {
            loop = 0;
        }
        inputX[loop] = x;
        inputY[loop] = y;
        inputZ[loop] = z;

        if (plot_iterator >= tableSize) index++;
        if (index == tableSize) index = 0;

        System.arraycopy(inputX, index, fftX, 0, tableSize - index);
        System.arraycopy(inputX, 0, fftX, tableSize - index, index);
        System.arraycopy(inputY, index, fftY, 0, tableSize - index);
        System.arraycopy(inputY, 0, fftY, tableSize - index, index);
        System.arraycopy(inputZ, index, fftZ, 0, tableSize - index);
        System.arraycopy(inputZ, 0, fftZ, tableSize - index, index);

        xSeries.clear();
        ySeries.clear();
        zSeries.clear();

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

                re = fftX[2 * j];
                im = fftX[2 * j + 1];
                xNormalizacja[j] = sqrt(re * re + im * im);

                re = fftY[2 * j];
                im = fftY[2 * j + 1];
                yNormalizacja[j] = sqrt(re * re + im * im);

                re = fftZ[2 * j];
                im = fftZ[2 * j + 1];
                zNormalizacja[j] = sqrt(re * re + im * im);
            }

        } else if (cursor == 1) {

            for (int i = 0; i < tableSize; i++) {
                x_Complex[i] = new Complex(fftX[i], 0);
                y_Complex[i] = new Complex(fftY[i], 0);
                z_Complex[i] = new Complex(fftZ[i], 0);
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

                re = x_algs4[j].re();
                im = x_algs4[j].im();
                xNormalizacja[j] = sqrt(re * re + im * im);

                re = y_algs4[j].re();
                im = y_algs4[j].im();
                yNormalizacja[j] = sqrt(re * re + im * im);

                re = z_algs4[j].re();
                im = z_algs4[j].im();
                zNormalizacja[j] = sqrt(re * re + im * im);
            }
        }

        max = 0.0;

        for (int j = 0; j < tableSize; j++) {
            if (xNormalizacja[j] > max || yNormalizacja[j] > max || zNormalizacja[j] > max) {
                if (xNormalizacja[j] > yNormalizacja[j]) {
                    if (xNormalizacja[j] > zNormalizacja[j])
                        max = xNormalizacja[j];
                    else max = zNormalizacja[j];
                } else if (yNormalizacja[j] > zNormalizacja[j])
                    max = yNormalizacja[j];
                else max = zNormalizacja[j];
            }
        }

        for (int j = 0; j < tableSize; j++) {
            freq = 50 * (double) j / (double) tableSize;

            xSeries.add(freq, xNormalizacja[j] / max);
            ySeries.add(freq, yNormalizacja[j] / max);
            zSeries.add(freq, zNormalizacja[j] / max);
        }


        multiRenderer.setXAxisMin(0);
        multiRenderer.setXAxisMax(25);

        loop++;
        plot_iterator++;

        mChart.repaint();

    }

    public void removeXAxis() {
        dataSet.removeSeries(xSeries);
        multiRenderer.removeSeriesRenderer(xSeriesRenderer);
        mChart.repaint();
    }

    public void addXAxis() {
        dataSet.addSeries(xSeries);
        multiRenderer.addSeriesRenderer(xSeriesRenderer);
        mChart.repaint();
    }

    public void removeYAxis() {
        dataSet.removeSeries(ySeries);
        multiRenderer.removeSeriesRenderer(ySeriesRenderer);
        mChart.repaint();
    }

    public void addYAxis() {
        dataSet.addSeries(ySeries);
        multiRenderer.addSeriesRenderer(ySeriesRenderer);
        mChart.repaint();
    }

    public void removeZAxis() {
        dataSet.removeSeries(zSeries);
        multiRenderer.removeSeriesRenderer(zSeriesRenderer);
        mChart.repaint();
    }

    public void addZAxis() {
        dataSet.addSeries(zSeries);
        multiRenderer.addSeriesRenderer(zSeriesRenderer);
        mChart.repaint();
    }

    private void setupChart() {
        //tworzymy obiekty serii danych
        xSeries = new XYSeries(getResources().getString(R.string.x_axis));
        ySeries = new XYSeries(getResources().getString(R.string.y_axis));
        zSeries = new XYSeries(getResources().getString(R.string.z_axis));
        XYSeries _Serie = new XYSeries("");
        _Serie.add(0, 0);

        dataSet = new XYMultipleSeriesDataset();

        dataSet.addSeries(xSeries);
        dataSet.addSeries(ySeries);
        dataSet.addSeries(zSeries);
        dataSet.addSeries(_Serie);

        xSeriesRenderer = new XYSeriesRenderer();
        xSeriesRenderer.setColor(Color.GREEN);
        xSeriesRenderer.setLineWidth(2);

        ySeriesRenderer = new XYSeriesRenderer();
        ySeriesRenderer.setColor(Color.CYAN);
        ySeriesRenderer.setLineWidth(2);

        zSeriesRenderer = new XYSeriesRenderer();
        zSeriesRenderer.setColor(Color.RED);
        zSeriesRenderer.setLineWidth(2);

        XYSeriesRenderer _SeriesRenderer = new XYSeriesRenderer();
        zSeriesRenderer.setLineWidth(0);

        multiRenderer = new XYMultipleSeriesRenderer();
        multiRenderer.setChartTitle(getResources().getString(R.string.spectrum));
        multiRenderer.setXTitle(getResources().getString(R.string.frequency));
        multiRenderer.setYTitle(getResources().getString(R.string.magnitude));

        multiRenderer.setChartTitleTextSize(getResources().getDimension(R.dimen.chart_title_text_size));
        multiRenderer.setAxisTitleTextSize(getResources().getDimension(R.dimen.axis_title_text_size));
        multiRenderer.setLegendTextSize(getResources().getDimension(R.dimen.legend_text_size));
        multiRenderer.setLabelsTextSize(getResources().getDimension(R.dimen.labels_text_size));
        multiRenderer.setPointSize(5);
        multiRenderer.setShowLegend(false);

        multiRenderer.setMargins(new int[]{
                (int) getResources().getDimension(R.dimen.margin_top),
                (int) getResources().getDimension(R.dimen.margin_left),
                (int) getResources().getDimension(R.dimen.margin_bottom),
                (int) getResources().getDimension(R.dimen.margin_right)
        });

        multiRenderer.setBackgroundColor(Color.BLACK);
        multiRenderer.setApplyBackgroundColor(true);

        multiRenderer.setShowGrid(true);
        multiRenderer.setPanEnabled(false, false);
        multiRenderer.setExternalZoomEnabled(false);
        multiRenderer.setZoomEnabled(false, false);

        multiRenderer.addSeriesRenderer(xSeriesRenderer);
        multiRenderer.addSeriesRenderer(ySeriesRenderer);
        multiRenderer.addSeriesRenderer(zSeriesRenderer);
        multiRenderer.addSeriesRenderer(_SeriesRenderer);
        mChart = (GraphicalView) ChartFactory.getLineChartView(getActivity().getBaseContext(), dataSet, multiRenderer);
        chart_container.addView(mChart);
    }

}
