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
public class SCAN implements DiskScheduler {
    private final int[] requestString;
    private final int numCilindros;
    private final int initCilindro;
    private int turnPoint;

    private SCAN(int[] requestString, int numCilindros, int initCilindro) {
        this.requestString = requestString;
        this.numCilindros = numCilindros;
        this.initCilindro = initCilindro;
    }

    public static void main(String[] args) {
        int[] requestString = {98, 37, 65, 67, 14, 122, 124, 183};
        int numCilindros = 199;
        int initCilindro = 53;

        DiskScheduler SCAN = new SCAN(requestString, numCilindros, initCilindro);
        System.out.println("Número de cilindros percorridos " + SCAN.getClass().getName() + " : " + SCAN.serviceRequests());
        SCAN.printGraph("SCAN.jpg");
    }

    public static void test(int[] requestString, int numCilindros, int initCilindro) {
        DiskScheduler SCAN = new SCAN(requestString, numCilindros, initCilindro);
        System.out.println("Número de cilindros percorridos " + SCAN.getClass().getName() + " : " + SCAN.serviceRequests());
        SCAN.printGraph("SCAN.jpg");
    }

    @Override
    public int serviceRequests() {
        // Clonando o vetor para caso algum metodo altere o original.
        int[] requestString = this.requestString.clone();

        int result = 0;

        // Ordenamos o vetor para poder iteragir mais facil sobre ele
        Arrays.sort(requestString);

        //Adicionando a distancia entre o começo e o primeiro ponto (primeiro solicitado ou o inicial, o que vier antes)
        result += requestString[0] < initCilindro ? requestString[0] : initCilindro;

        //Adicionando a distancia entre o fim e o ultimo ponto
        result += numCilindros - (requestString[requestString.length - 1] > initCilindro ?
                requestString[requestString.length - 1] : initCilindro);

        for (int i = 0; i < requestString.length - 1; i++) {
            if (requestString[i] < initCilindro && requestString[i + 1] < initCilindro)
                result += requestString[i + 1] - requestString[i];
            else if (requestString[i] > initCilindro && requestString[i + 1] > initCilindro)
                result += requestString[i + 1] - requestString[i];
            else {
                if (initCilindro <= numCilindros / 2) {
                    result += requestString[i + 1];
                } else {
                    result += requestString[i + 1] - initCilindro;
                }
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

        XYSeries series = new XYSeries("SCAN");

        series.add(y_axis, initCilindro);

        if (initCilindro <= numCilindros / 2) {
            for (int i = turnPoint; i >= 0; i--) {
                series.add(++y_axis, requestString[i]);
            }

            series.add(++y_axis, 0);

            for (int i = turnPoint + 1; i < requestString.length; i++) {
                series.add(++y_axis, requestString[i]);
            }
            series.add(++y_axis, numCilindros);
        } else {
            for (int i = turnPoint; i < requestString.length; i++) {
                series.add(++y_axis, requestString[i]);
            }

            series.add(++y_axis, numCilindros);

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
                "SCAN Scheduler Algorithm",
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
