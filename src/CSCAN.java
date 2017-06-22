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
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Douglas Leite
 *         Parte do projeto T2SisOp
 *         <p>
 *         13/06/2017.
 */
public class CSCAN implements DiskScheduler {
    private int[] requests;
    private int numCilindros;
    private int initCilindro;
    private int turnPoint;

    private CSCAN(int[] requests, int numCilindros, int initCilindro) {
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

        //Adicionando a distancia entre o começo e o primeiro ponto (primeiro solicitado ou o inicial, o que vier antes)
        result += requests[0] < initCilindro ? requests[0] : initCilindro;

        //Adicionando a distancia entre o fim e o ultimo ponto
        result += numCilindros - (requests[requests.length - 1] > initCilindro ?
                requests[requests.length - 1] : initCilindro);


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

        XYSeries series1 = new XYSeries("FCFS");
        XYSeries series2 = new XYSeries("FCFS2");
        XYSeries connectionSeries = new XYSeries("Connection");

        series1.add(y_axis, initCilindro);

        if (initCilindro <= numCilindros / 2) {
            for (int i = turnPoint; i >= 0; i--) {
                series1.add(++y_axis, requests[i]);
            }

            series1.add(++y_axis, 0);
            connectionSeries.add(y_axis, 0);
            connectionSeries.add(y_axis, numCilindros);
            series2.add(y_axis, numCilindros);

            for (int i = requests.length - 1; i > turnPoint; i--) {
                series2.add(++y_axis, requests[i]);
            }
        } else {
            for (int i = requests.length - 1; i >= turnPoint; i--) {
                series1.add(++y_axis, requests[i]);
            }

            series1.add(++y_axis, numCilindros);
            connectionSeries.add(y_axis, 0);
            connectionSeries.add(y_axis, numCilindros);
            series2.add(y_axis, 0);

            for (int i = 0; i < turnPoint; i++) {
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
                "CSCAN Scheduler Algorithm",
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
        int[] requests = null;
        int numCilindros = 0;
        int initCilindro = 0;

        try(BufferedReader br = new BufferedReader(new FileReader(args[0]))) {
            String line = br.readLine();
            numCilindros = Integer.parseInt(line);

            line = br.readLine();
            initCilindro = Integer.parseInt(line);

            java.util.List<Integer> requestList = new ArrayList<>();
            line = br.readLine();
            for(String s : line.split(" ")){
                requestList.add(Integer.parseInt(s));
            }
            //iniciando o vetor com o tamanho necessario
            requests = new int[requestList.size()];

            //Transforma a lista para vetor de inteiros
            requests = requestList.stream().mapToInt(i->i).toArray();
        } catch (Exception e) {
            e.printStackTrace();
        }

        DiskScheduler CSCAN = new CSCAN(requests, numCilindros, initCilindro);
        System.out.println("Número de cilindros percorridos " + CSCAN.getClass().getName() + " : " + CSCAN.serviceRequests());
        CSCAN.printGraph("CSCAN.jpg");
    }

}
