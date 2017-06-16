/*
 * @author Douglas Leite
 * Parte do projeto T2SisOp
 * <p>
 * 13/06/2017.
 */

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
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.lang.Math.abs;

public class FCFS implements DiskScheduler {
    private final int[] requestString;
    private final int numCilindros;
    private final int initCilindro;

    private FCFS(int[] requestString, int numCilindros, int initCilindro) {
        this.requestString = requestString;
        this.numCilindros = numCilindros;
        this.initCilindro = initCilindro;
    }

    public static void main(String[] args) {
        int[] requestString = {95, 180, 34, 119, 11, 123, 62, 64};
        int numCilindros = 199;
        int initCilindro = 50;

        DiskScheduler FCFS = new FCFS(requestString, numCilindros, initCilindro);
        System.out.println("Número de cilindros percorridos " + FCFS.getClass().getName() + " : " + FCFS.serviceRequests());
        FCFS.printGraph("FCFS.jpg");
    }

    public static void test(int[] requestString, int numCilindros, int initCilindro) {
        DiskScheduler FCFS = new FCFS(requestString, numCilindros, initCilindro);
        System.out.println("Número de cilindros percorridos " + FCFS.getClass().getName() + " : " + FCFS.serviceRequests());
        FCFS.printGraph("FCFS.jpg");
    }

    @Override
    public int serviceRequests() {
        // Clonando o vetor para caso algum metodo altere o original.
        int[] requestString = this.requestString.clone();

        int result;

        result = abs(initCilindro - requestString[0]);
        for (int i = 0; i < requestString.length - 1; i++) {
            result += abs(requestString[i] - requestString[i + 1]);
        }

        return result;
    }

    @Override
    public void printGraph(String filename) {
        // Clonando o vetor para caso algum metodo altere o original.
        int[] requestString = this.requestString.clone();

        int i;
        int y_axis = 0;

        XYSeries series = new XYSeries("FCFS");

        /* Adiciona o pontos XY do gráfico de linhas. */
        series.add(y_axis, initCilindro);

        for (i = 0; i < requestString.length; i++) {
            series.add(y_axis + ((i + 1)), requestString[i]);
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
            Logger.getLogger(FCFS.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
