/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * @author crmoratelli
 */
public interface DiskScheduler {
    int chartWidth = 600;
    int chartHeight = 300;

    int serviceRequests();
    void printGraph(String filename);
}
