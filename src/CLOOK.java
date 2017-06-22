import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;


import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Douglas Leite
 *         Parte do projeto T2SisOp
 *         <p>
 *         13/06/2017.
 */
public class CLOOK implements DiskScheduler {
    private int[] requests;
    private int numCilindros;
    private int initCilindro;
    private int turnPoint;

    private CLOOK(int[] requests, int numCilindros, int initCilindro) {
        this.requests = requests;
        this.numCilindros = numCilindros;
        this.initCilindro = initCilindro;
    }

    @Override
    public int serviceRequests() {
        // Clonando o vetor para caso algum metodo altere o original.
        int[] requests = this.requests.clone();

        int result = 0;

        // Ordenamos o vetor para poder iteragir mais facil sobre ele
        Arrays.sort(requests);

        for (int i = 0; i < requests.length - 1; i++) {
            if ((requests[i] < initCilindro && requests[i + 1] < initCilindro) ||
                    (requests[i] > initCilindro && requests[i + 1] > initCilindro))
                result += requests[i + 1] - requests[i];
            else {
                result += initCilindro - requests[i];
                turnPoint = initCilindro <= numCilindros / 2 ? i : i + 1;
            }
        }

        return result;
    }

    @Override
    public void printGraph(String filename) {
        // Clonando o vetor para caso algum metodo altere o original.
        int[] requests = this.requests.clone();

        // Ordenamos o vetor para poder iteragir mais facil sobre ele
        Arrays.sort(requests);

        int y_axis = 0;

        XYSeries series1 = new XYSeries("CLOOK");
        XYSeries series2 = new XYSeries("CLOOK2");
        XYSeries connectionSeries = new XYSeries("Connection");


        series1.add(y_axis, initCilindro);

        if (initCilindro <= numCilindros / 2) {
            for (int i = turnPoint; i >= 0; i--) {
                series1.add(++y_axis, requests[i]);
            }

            connectionSeries.add(y_axis, requests[0]);
            connectionSeries.add(y_axis, requests[requests.length - 1]);
            series2.add(y_axis, requests[requests.length - 1]);

            for (int i = requests.length - 2; i > turnPoint; i--) {
                series2.add(++y_axis, requests[i]);
            }
        } else {
            for (int i = requests.length - 1; i >= turnPoint; i--) {
                series1.add(++y_axis, requests[i]);
            }

            connectionSeries.add(y_axis, requests[requests.length - 1]);
            connectionSeries.add(y_axis, requests[0]);
            series2.add(y_axis, requests[0]);

            for (int i = 1; i < turnPoint; i++) {
                series2.add(++y_axis, requests[i]);
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
        renderer.setSeriesPaint(0, Color.CYAN);

        renderer.setSeriesStroke(1, new BasicStroke(2.0f));
        renderer.setSeriesPaint(1, Color.CYAN);

        renderer.setSeriesStroke(2, new BasicStroke(1.0f));
        renderer.setSeriesPaint(2, Color.BLACK);

        plot.setRenderer(renderer);

        try {
            ChartUtilities.saveChartAsJPEG(new File(filename), chart, chartWidth, chartHeight);
        } catch (IOException ex) {
            Logger.getLogger(SSTF.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String[] args) {
        int[] request = null;
        int numCilindros = 0;
        int initCilindro = 0;

        try(BufferedReader br = new BufferedReader(new FileReader(args[0]))) {
            String line = br.readLine();
            numCilindros = Integer.parseInt(line);

            line = br.readLine();
            initCilindro = Integer.parseInt(line);

            List<Integer> requestList = new ArrayList<>();
            line = br.readLine();
            for(String s : line.split(" ")){
                requestList.add(Integer.parseInt(s));
            }
            //iniciando o vetor com o tamanho necessario
            request = new int[requestList.size()];

            //Transforma a lista para vetor de inteiros
            request = requestList.stream().mapToInt(i->i).toArray();
        } catch (Exception e) {
            e.printStackTrace();
        }

        DiskScheduler CLOOK = new CLOOK(request, numCilindros, initCilindro);
        System.out.println("Número de cilindros percorridos " + CLOOK.getClass().getName() + " : " + CLOOK.serviceRequests());
        CLOOK.printGraph("CLOOK.jpg");
    }
}
