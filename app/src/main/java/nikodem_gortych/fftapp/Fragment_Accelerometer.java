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


public class Fragment_Accelerometer extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public LinearLayout chart_container;
    View view;
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
    private int plotIterator = 0;

    public Fragment_Accelerometer() {
        // Required empty public constructor
    }

    public static Fragment_Accelerometer newInstance(String param1, String param2) {
        Fragment_Accelerometer fragment = new Fragment_Accelerometer();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
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

        view = inflater.inflate(R.layout.fragment_accelerometer, container, false);
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
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
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

    public void readFromAccelerometer(float x, float y, float z) {
        xSeries.add(plotIterator, x);
        ySeries.add(plotIterator, y);
        zSeries.add(plotIterator, z);

        multiRenderer.setXAxisMin(plotIterator - Fragment_FFT.getTableSize());
        multiRenderer.setXAxisMax(plotIterator);

        plotIterator++;

        mChart.repaint();
    }

    public void clearSeries() {
        xSeries.clear();
        ySeries.clear();
        zSeries.clear();
        plotIterator = 0;
    }

    public void setupChart() {
        //tworzymy obiekty serii danych
        xSeries = new XYSeries(getResources().getString(R.string.x_axis));
        ySeries = new XYSeries(getResources().getString(R.string.y_axis));
        zSeries = new XYSeries(getResources().getString(R.string.z_axis));
        XYSeries _Series = new XYSeries("");

        _Series.add(0, 0);
        //tworzymy obiekt zbioru danych
        dataSet = new XYMultipleSeriesDataset();

        //i dodajemy do niego nasze serie danych
        dataSet.addSeries(xSeries);
        dataSet.addSeries(ySeries);
        dataSet.addSeries(zSeries);
        dataSet.addSeries(_Series);

        //tworzymy obiekty do renderowania serii danych
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
        _SeriesRenderer.setLineWidth(0);

        multiRenderer = new XYMultipleSeriesRenderer();
        multiRenderer.setChartTitle(getResources().getString(R.string.accelerometer));
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
