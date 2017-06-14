import com.sun.deploy.util.ArrayUtil;
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

/**
 * Criado por Douglas Leite
 * Parte do projeto T2SisOp
 * <p>
 * 13/06/2017.
 */
public class SSTF implements DiskScheduler {
    private int[] requestString;
    private int numCilindros;
    private int initCilindro;

    public SSTF(int[] requestString, int numCilindros, int initCilindro) {
        this.requestString = requestString;
        this.numCilindros = numCilindros;
        this.initCilindro = initCilindro;
    }

    @Override
    public int serviceRequests() {
        // Clonando o vetor pois o método altera o originial
        int[] requestString = this.requestString.clone();

        int result = 0;
        int request = -1;
        int lastIndex = requestString.length;
        int dist = numCilindros + 1;
        int position = initCilindro;

        for (int j = 0; j < requestString.length; j++) {
            for (int i = 0; i < lastIndex; i++) {
                if (requestString[i] > -1) {
                    if (abs(position - requestString[i]) < dist) {
                        dist = abs(position - requestString[i]);
                        request = i;
                    }
                }
            }
            // Reduz o tamanho do vetor em 1 para o loop interno
            lastIndex--;

            // Adiciona a distancia percorrida no resultado
            result += dist;
            dist = numCilindros + 1;

            // Salva a posição atual
            position = requestString[request];
            // Puxa do "fim" do vetor o ultimo numero que ainda não foi processado
            requestString[request] = requestString[lastIndex];
        }

        return result;
    }

    @Override
    public void printGraph(String filename) {
        // Clonando o vetor pois o método altera o originial
        int[] requestString = this.requestString.clone();

        int y_axis = 0;
        int request = -1;
        int lastIndex = requestString.length;
        int dist = numCilindros + 1;

        XYSeries series = new XYSeries("FCFS");

        int position = initCilindro;

        /* Adiciona o pontos XY do gráfico de linhas. */
        series.add(y_axis, initCilindro);

        for (int j = 0; j < requestString.length; j++) {
            for (int i = 0; i < lastIndex; i++) {
                if (requestString[i] > -1) {
                    if (abs(position - requestString[i]) < dist) {
                        dist = abs(position - requestString[i]);
                        request = i;
                    }
                }
            }
            // Reduz o tamanho do vetor em 1 para o loop interno
            lastIndex--;

            dist = numCilindros + 1;

            // Salva a posição atual
            position = requestString[request];
            // Puxa do "fim" do vetor o ultimo numero que ainda não foi processado
            requestString[request] = requestString[lastIndex];
            series.add(y_axis + ((j + 1)), position);
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
        plot.setRenderer(renderer);

        try {
            ChartUtilities.saveChartAsJPEG(new File(filename), chart, 500, 300);
        } catch (IOException ex) {
            Logger.getLogger(SSTF.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String[] args) {
        int[] requestString = {98, 183, 37, 122, 14, 126, 65, 67};
        int numCilindros = 200;
        int initCilindro = 53;

        DiskScheduler SSTF = new SSTF(requestString, numCilindros, initCilindro);
        System.out.println("Número de cilindros percorridos: " + SSTF.serviceRequests());
        SSTF.printGraph("SSTF.jpg");

    }
}
