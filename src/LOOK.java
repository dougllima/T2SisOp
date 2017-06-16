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
public class LOOK implements DiskScheduler {
    private final int[] requestString;
    private final int numCilindros;
    private final int initCilindro;
    private int turnPoint;

    private LOOK(int[] requestString, int numCilindros, int initCilindro) {
        this.requestString = requestString;
        this.numCilindros = numCilindros;
        this.initCilindro = initCilindro;
    }

    public static void main(String[] args) {
        int[] requestString = {98, 183, 37, 122, 14, 124, 65, 67};
        int numCilindros = 200;
        int initCilindro = 53;

        DiskScheduler LOOK = new LOOK(requestString, numCilindros, initCilindro);
        System.out.println("Número de cilindros percorridos " + LOOK.getClass().getName() + " : " + LOOK.serviceRequests());
        LOOK.printGraph("LOOK.jpg");
    }

    public static void test(int[] requestString, int numCilindros, int initCilindro) {
        DiskScheduler LOOK = new LOOK(requestString, numCilindros, initCilindro);
        System.out.println("Número de cilindros percorridos " + LOOK.getClass().getName() + " : " + LOOK.serviceRequests());
        LOOK.printGraph("LOOK.jpg");
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
                result += requestString[0] + requestString[i + 1];
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

        XYSeries series = new XYSeries("FCFS");

        series.add(y_axis, initCilindro);

        if (initCilindro <= numCilindros / 2) {
            for (int i = turnPoint; i >= 0; i--) {
                series.add(++y_axis, requestString[i]);
            }
            for (int i = turnPoint + 1; i < requestString.length; i++) {
                series.add(++y_axis, requestString[i]);
            }
            series.add(++y_axis, numCilindros);
        } else {
            for (int i = turnPoint; i < requestString.length; i++) {
                series.add(++y_axis, requestString[i]);
            }
            for (int i = turnPoint - 1; i >= 0; i--) {
                series.add(++y_axis, requestString[i]);
            }
            series.add(++y_axis, 0);
        }

        /* Adiciona a serie criada a um SeriesCollection. */
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(series);

        /* Gera o gráfico de linhas */
        JFreeChart chart = ChartFactory.createXYLineChart(
            /* Title */
                "FCFS Scheduler Algorithm",
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

        plot.setRenderer(renderer);

        try {
            ChartUtilities.saveChartAsJPEG(new File(filename), chart, chartWidth, chartHeight);
        } catch (IOException ex) {
            Logger.getLogger(SSTF.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
