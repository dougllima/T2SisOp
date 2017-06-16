import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Douglas Leite
 *         Parte do projeto T2SisOp
 *         <p>
 *         13/06/2017.
 */
public class CLOOK implements DiskScheduler {
    private final int[] requestString;
    private final int numCilindros;
    private final int initCilindro;
    private int turnPoint;

    private CLOOK(int[] requestString, int numCilindros, int initCilindro) {
        this.requestString = requestString;
        this.numCilindros = numCilindros;
        this.initCilindro = initCilindro;
    }

    public static void main(String[] args) {
        int[] requestString = {98, 183, 37, 122, 14, 124, 65, 67};
        int numCilindros = 200;
        int initCilindro = 53;

        DiskScheduler CLOOK = new CLOOK(requestString, numCilindros, initCilindro);
        System.out.println("Número de cilindros percorridos " + CLOOK.getClass().getName() + " : " + CLOOK.serviceRequests());
        CLOOK.printGraph("CLOOK.jpg");
    }

    public static void test(int[] requestString, int numCilindros, int initCilindro) {
        DiskScheduler CLOOK = new CLOOK(requestString, numCilindros, initCilindro);
        System.out.println("Número de cilindros percorridos " + CLOOK.getClass().getName() + " : " + CLOOK.serviceRequests());
        CLOOK.printGraph("CLOOK.jpg");
    }

    @Override
    public int serviceRequests() {
        // Clonando o vetor para caso algum metodo altere o original.
        int[] requestString = this.requestString.clone();

        int result = 0;

        // Ordenamos o vetor para poder iteragir mais facil sobre ele
        Arrays.sort(requestString);

        for (int i = 0; i < requestString.length - 1; i++) {
            if ((requestString[i] < initCilindro && requestString[i + 1] < initCilindro) ||
                    (requestString[i] > initCilindro && requestString[i + 1] > initCilindro))
                result += requestString[i + 1] - requestString[i];
            else {
                result += initCilindro - requestString[i];
                turnPoint = initCilindro <= numCilindros / 2 ? i : i + 1;
            }
        }

        return result;
    }

    @Override
    public void printGraph(String filename) {
        // Clonando o vetor para caso algum metodo altere o original.
        int[] requestString = this.requestString.clone();

        // Ordenamos o vetor para poder iteragir mais facil sobre ele
        Arrays.sort(requestString);

        int y_axis = 0;

        XYSeries series1 = new XYSeries("CLOOK");
        XYSeries series2 = new XYSeries("CLOOK2");
        XYSeries connectionSeries = new XYSeries("Connection");


        series1.add(y_axis, initCilindro);

        if (initCilindro <= numCilindros / 2) {
            for (int i = turnPoint; i >= 0; i--) {
                series1.add(++y_axis, requestString[i]);
            }

            connectionSeries.add(y_axis, requestString[0]);
            connectionSeries.add(y_axis, requestString[requestString.length - 1]);
            series2.add(y_axis, requestString[requestString.length - 1]);

            for (int i = requestString.length - 2; i > turnPoint; i--) {
                series2.add(++y_axis, requestString[i]);
            }
        } else {
            for (int i = requestString.length - 1; i >= turnPoint; i--) {
                series1.add(++y_axis, requestString[i]);
            }

            connectionSeries.add(y_axis, requestString[requestString.length - 1]);
            connectionSeries.add(y_axis, requestString[0]);
            series2.add(y_axis, requestString[0]);

            for (int i = 1; i < turnPoint; i++) {
                series2.add(++y_axis, requestString[i]);
            }

        }

        /* Adiciona a serie criada a um SeriesCollection. */
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(series1);
        dataset.addSeries(series2);
        dataset.addSeries(connectionSeries);

        /* Gera o gráfico de linhas */
        JFreeChart chart = ChartFactory.createXYLineChart(
            /* Title */
                "CLOOK Scheduler Algorithm",
            /* Title x*/
                "",
            /* Title y */
                "Cilindro",
                dataset,
            /* Plot Orientation */
                PlotOrientation.HORIZONTAL,
            /* Show Legend */
                false,
            /* Use tooltips */
                false,
            /* Configure chart to generate URLs? */
                false
        );

        /* Configura a espessura da linha do gráfico  */
        XYPlot plot = chart.getXYPlot();

        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setSeriesStroke(0, new BasicStroke(2.0f));
        renderer.setSeriesPaint(0, Color.red);

        renderer.setSeriesStroke(1, new BasicStroke(2.0f));
        renderer.setSeriesPaint(1, Color.red);

        renderer.setSeriesStroke(2, new BasicStroke(1.0f));
        renderer.setSeriesPaint(2, Color.blue);

        plot.setRenderer(renderer);

        try {
            ChartUtilities.saveChartAsJPEG(new File(filename), chart, chartWidth, chartHeight);
        } catch (IOException ex) {
            Logger.getLogger(SSTF.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
