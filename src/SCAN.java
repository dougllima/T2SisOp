import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.util.ShapeList;
import org.jfree.util.ShapeUtilities;

import java.awt.*;
import java.awt.geom.RectangularShape;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.lang.Math.abs;

/**
 * @author Douglas Leite
 *         Parte do projeto T2SisOp
 *         <p>
 *         13/06/2017.
 */
public class SCAN implements DiskScheduler {
    private int[] requestString;
    private int numCilindros;
    private int initCilindro;
    private int turnPoint;

    public SCAN(int[] requestString, int numCilindros, int initCilindro) {
        this.requestString = requestString;
        this.numCilindros = numCilindros;
        this.initCilindro = initCilindro;
    }

    @Override
    public int serviceRequests() {
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
        // Clonando o vetor pois o método altera o originial
        int[] requestString = this.requestString.clone();

        int y_axis = 0;

        XYSeries series1 = new XYSeries("FCFS");
        XYSeries series2 = new XYSeries("FCFS2");
        XYSeries connectionSeries = new XYSeries("Connection");

        series1.add(y_axis, initCilindro);

        if (initCilindro <= numCilindros / 2) {
            for (int i = turnPoint; i >= 0; i--) {
                series1.add(++y_axis, requestString[i]);
            }

            series1.add(++y_axis, 0);
            connectionSeries.add(y_axis, 0);
            connectionSeries.add(y_axis, numCilindros);
            series2.add(y_axis, numCilindros);

            for (int i = requestString.length - 1; i > turnPoint; i--) {
                series2.add(++y_axis, requestString[i]);
            }
        } else {
            for (int i = requestString.length - 1; i >= turnPoint; i--) {
                series1.add(++y_axis, requestString[i]);
            }

            series1.add(++y_axis, numCilindros);
            connectionSeries.add(y_axis, 0);
            connectionSeries.add(y_axis, numCilindros);
            series2.add(y_axis, 0);

            for (int i = 0; i < turnPoint; i++) {
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
        renderer.setSeriesPaint(0, Color.red);
        renderer.setSeriesStroke(0, new BasicStroke(2.0f));

        renderer.setSeriesPaint(1, Color.red);
        renderer.setSeriesStroke(1, new BasicStroke(2.0f));

        renderer.setSeriesPaint(2, Color.blue);
        renderer.setSeriesStroke(2, new BasicStroke(1.0f));

        plot.setRenderer(renderer);

        try {
            ChartUtilities.saveChartAsJPEG(new File(filename), chart, 500, 300);
        } catch (IOException ex) {
            Logger.getLogger(SSTF.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String[] args) {
        int[] requestString = {14, 37, 65, 67, 98, 122, 124, 183};
        int numCilindros = 199;
        int initCilindro = 151;

        DiskScheduler SCAN = new SCAN(requestString, numCilindros, initCilindro);
        System.out.println("Número de cilindros percorridos: " + SCAN.serviceRequests());
        SCAN.printGraph("SCAN.jpg");
    }
}
