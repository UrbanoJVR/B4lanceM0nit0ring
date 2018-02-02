/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Views;

import Clases.Control;
import Clases.Movimiento;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.DefaultIntervalXYDataset;
import org.jfree.data.xy.IntervalXYDataset;

/**
 *
 * @author Urbano
 */
public class Main extends javax.swing.JFrame {

    //VARIABLES MÓDULO GENERAL -------------------------
    //VARIABLES MÓDULO GRÁFICOS ------------------------
    boolean graficosYaIniciada = false; //Inicializamos a false porque todavía no se ha levantado el componente
    DefaultCategoryDataset actualData = this.loadDefaultData(); //Inicializamos a default hasta que se seleccione uno diferente
    String actualTitle = "Evolución de saldo"; //Valores por defecto, a cambiar cuando se seteen nuevos gráficos
    String actualX = "Movimientos";
    String actualY = "Saldo";
    //VARIABLES MÓDULO CUENTAS -------------------------
    int mesActual; //0 es enero
    int actualYear; 
    ArrayList<Movimiento> movimientosMesActual;
    ArrayList<Movimiento> ingresos;
    ArrayList<Movimiento> gastos;

    /**
     * Creates new form Main
     */
    public Main() throws IOException, ParseException {
        initComponents();
        //Control.leerXls();
        Control.loadConfig();
        Control.leerExcel(new File("Ultimos999.xls"));
        Control.loadDefaultCategories();
        //Control.loadCategories();
        Control.categorize();
        this.escribirTablaGeneral(Control.movimientos);
        this.texto.setText(UIManager.getSystemLookAndFeelClassName());
        //this.createDefaultGraphic();
    }

    //OPERACIONES MÓDULO GENERAL --------------------------------------------------------------------------------------------------------------------------
    public void escribirTablaGeneral(ArrayList<Movimiento> mov) {
        try {
            if (this.tabla.getRowCount() != 0) {
                DefaultTableModel m = (DefaultTableModel) this.tabla.getModel();
                m.setRowCount(0);
                this.tabla.setModel(m);
            }
            for (int i = 0; i < mov.size(); i++) {
                this.añadirFilaTablaGeneral();
                for (int j = 0; j < tabla.getColumnCount(); j++) {
                    DefaultTableCellRenderer tcr = new DefaultTableCellRenderer();
                    tcr.setHorizontalAlignment(SwingConstants.CENTER);
                    tabla.getColumnModel().getColumn(j).setCellRenderer(tcr);
                }
                //this.tabla.setDefaultRenderer(Double.class, new MiTableRender());
                this.tabla.setValueAt(Control.fechaToString(mov.get(i).getFecha()), i, 0);
                this.tabla.setValueAt(mov.get(i).getConcepto(), i, 1);
                this.tabla.setValueAt(mov.get(i).getCategoria(), i, 2);
                this.tabla.setValueAt(mov.get(i).getImporte(), i, 3);
                this.tabla.setValueAt(mov.get(i).getSaldo(), i, 4);
                //this.tabla.setDefaultRenderer(Double.class, new MiTableRender());
                //tabla.setDefaultRenderer(Object.class, new MiTableRender());
            }
            //tabla.setDefaultRenderer(Object.class, new MiTableRender());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error escribiendo tabla", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void añadirFilaTablaGeneral() {
        DefaultTableModel modelo = (DefaultTableModel) this.tabla.getModel();
        modelo.setRowCount(modelo.getRowCount() + 1);
    }
    //hoaaaaaaaaaa

    //FIN MÓDULO GENERAL -----------------------------------------------------------------------------------------------------------------------------------
    //OPERACIONES MÓDULO GRÁFICOS -----------------------------------------------------------------------------------------------------------------------------
    public void createDefaultGraphic() {
        JFreeChart defaultGraphic;
        DefaultCategoryDataset datos = new DefaultCategoryDataset();
        for (int i = Control.movimientos.size() - 1; i > 0; i = i - 1) {
            datos.addValue(Control.movimientos.get(i).getSaldo(), "Saldo cuenta", Control.movimientos.get(i).getFecha().toString());
        }
        defaultGraphic = ChartFactory.createLineChart("Evolucion de saldo", "Movimientos", "Cuantía", datos, PlotOrientation.VERTICAL, true, true, false);
        ChartPanel panel = new ChartPanel(defaultGraphic);
        this.graphicPanel.setLayout(new java.awt.BorderLayout());
        this.graphicPanel.add(panel);
        this.graphicPanel.validate();
    }

    public DefaultCategoryDataset loadDefaultData() {
        DefaultCategoryDataset data = new DefaultCategoryDataset();
        for (int i = Control.movimientos.size() - 1; i > 0; i = i - 1) {
            data.addValue(Control.movimientos.get(i).getSaldo(), "Saldo cuenta", Control.movimientos.get(i).getFecha().toString());
        }
        return data;
    }

    public void setGraphic(DefaultCategoryDataset dat, String type, String title, String x, String y) {
        switch (type) {
            case "linea":
                DefaultCategoryDataset data = dat;
                JFreeChart lineGraphic = ChartFactory.createLineChart(title, x, y, data, PlotOrientation.VERTICAL, true, true, false);
                ChartPanel panel = new ChartPanel(lineGraphic);
                this.graphicPanel.setLayout(new java.awt.BorderLayout());
                this.graphicPanel.add(panel);
                this.graphicPanel.validate();
                break;
            case "barras":
                break;
            case "area":
                break;
            case "linea3d":
                break;
            case "barras3d":
                break;
            default:
                break;
        }
    }

    //FIN MÓDULO GRÁFICOS ---------------------------------------------------------------------------------------------------------------------------------------
    //OPERACIONES MÓDULO CUENTAS -----------------------------------------------------------------------------------------------------------------------------
    public void extraerMovimientosMes(int mes, int year) {
        //El mes 0 es enero
        try {
            this.movimientosMesActual = new ArrayList<>();
            for (int i = 0; i < Control.movimientos.size(); i++) {
                if ((Control.movimientos.get(i).getFecha().getMonth() == mes) && (Control.movimientos.get(i).getFecha().getYear() == year)) {
                    //El movimiento en el que estamos pertenece al mes actual
                    this.movimientosMesActual.add(Control.movimientos.get(i));
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error extrayendo movimientos\n" + e.getMessage(), "Error módulo cuentas", JOptionPane.ERROR_MESSAGE);
        }
        this.extraerIngresos();
        this.extraerGastos();
    }

    public void extraerIngresos() {
        try {
            this.ingresos = new ArrayList<>();
            for (int i = 0; i < this.movimientosMesActual.size(); i++) {
                if (this.movimientosMesActual.get(i).getImporte() > 0) {
                    //El movimiento es un ingreso
                    this.ingresos.add(this.movimientosMesActual.get(i));
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error extrayendo ingresos\n" + e.getMessage(), "Error módulo cuentas", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void extraerGastos() {
        try {
            this.gastos = new ArrayList<>();
            for (int i = 0; i < this.movimientosMesActual.size(); i++) {
                if (this.movimientosMesActual.get(i).getImporte() < 0) {
                    //El movimiento es un gasto
                    this.gastos.add(this.movimientosMesActual.get(i));
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error extrayendo gastos\n" + e.getMessage(), "Error módulo cuentas", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void escribirTablaIngresos() {
        try {
            DefaultTableModel modelo = (DefaultTableModel) this.tablaIngresosCuentas.getModel();
            modelo.setRowCount(0);
            for (int i = 0; i < this.ingresos.size(); i++) {
                modelo.setRowCount(modelo.getRowCount() + 1);
                this.tablaIngresosCuentas.setValueAt(Control.fechaToString(this.ingresos.get(i).getFecha()), i, 0);
                this.tablaIngresosCuentas.setValueAt(this.ingresos.get(i).getConcepto(), i, 1);
                this.tablaIngresosCuentas.setValueAt(this.ingresos.get(i).getCategoria(), i, 2);
                this.tablaIngresosCuentas.setValueAt(this.ingresos.get(i).getImporte(), i, 3);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error escribiendo ingresos\n " + e.getMessage(), "Error módulo cuentas", JOptionPane.ERROR_MESSAGE);
        }
    }

    //FIN MÓDULO CUENTAS ----------------------------------------------------------------------------------------------------------------------------------------
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        botonesInformesToggleButon = new javax.swing.ButtonGroup();
        paneles = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tabla = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        añadirButon = new javax.swing.JButton();
        editarButon = new javax.swing.JButton();
        eliminarButon = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        copiarButon = new javax.swing.JButton();
        filtrarButon = new javax.swing.JButton();
        refrescarButon = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JSeparator();
        jScrollPane3 = new javax.swing.JScrollPane();
        texto = new javax.swing.JTextPane();
        refrescarButon1 = new javax.swing.JButton();
        refrescarButon2 = new javax.swing.JButton();
        panel_Informes = new javax.swing.JPanel();
        graphicPanel = new javax.swing.JPanel();
        butonPanelRight = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        butonPanelDown = new javax.swing.JPanel();
        jSeparator3 = new javax.swing.JSeparator();
        saldoButon = new javax.swing.JToggleButton();
        ahorroButon = new javax.swing.JToggleButton();
        gastosButon = new javax.swing.JToggleButton();
        categoriaButon = new javax.swing.JToggleButton();
        categoriasInformesComboBox = new javax.swing.JComboBox<>();
        jButton9 = new javax.swing.JButton();
        jButton10 = new javax.swing.JButton();
        jButton11 = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        tablaIngresosCuentas = new javax.swing.JTable();
        jPanel6 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablaGastosCuentas = new javax.swing.JTable();
        jButton7 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        jPanel7 = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        tabalCategoriasCuentas = new javax.swing.JTable();
        jSeparator4 = new javax.swing.JSeparator();
        jButton12 = new javax.swing.JButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        ArchivoMenu = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem4 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenuItem3 = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(1635, 1035));

        paneles.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                panelesMouseClicked(evt);
            }
        });
        paneles.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                panelesComponentShown(evt);
            }
        });

        tabla.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "FECHA", "CONCEPTO", "CATEGORÍA", "IMPORTE", "SALDO"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Double.class, java.lang.Double.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tabla.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(tabla);

        jPanel1.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        añadirButon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Others/añadirMovimiento.png"))); // NOI18N
        añadirButon.setText("Importar");
        añadirButon.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        añadirButon.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        añadirButon.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                añadirButonActionPerformed(evt);
            }
        });

        editarButon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Others/editarMovimiento.png"))); // NOI18N
        editarButon.setText("Exportar");
        editarButon.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        editarButon.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        eliminarButon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Others/eliminarMovimiento.png"))); // NOI18N
        eliminarButon.setText("Vaciar");
        eliminarButon.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        eliminarButon.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);

        copiarButon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Others/copiar.png"))); // NOI18N
        copiarButon.setText("Copiar");
        copiarButon.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        copiarButon.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        filtrarButon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Others/filtros.png"))); // NOI18N
        filtrarButon.setText("Filtros");
        filtrarButon.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        filtrarButon.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        refrescarButon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Others/crearBackup.png"))); // NOI18N
        refrescarButon.setText("Crear backup");
        refrescarButon.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        refrescarButon.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        refrescarButon.setName(""); // NOI18N
        refrescarButon.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        jSeparator2.setOrientation(javax.swing.SwingConstants.VERTICAL);

        jScrollPane3.setViewportView(texto);

        refrescarButon1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Others/refresh.png"))); // NOI18N
        refrescarButon1.setText("Refrescar");
        refrescarButon1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        refrescarButon1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        refrescarButon2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Others/cargarBackup.png"))); // NOI18N
        refrescarButon2.setText("Cargar backup");
        refrescarButon2.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        refrescarButon2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        refrescarButon2.setName(""); // NOI18N
        refrescarButon2.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(añadirButon, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(editarButon, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(eliminarButon, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(copiarButon, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(filtrarButon, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(refrescarButon1, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(refrescarButon)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(refrescarButon2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 677, Short.MAX_VALUE)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 425, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane3)
                    .addComponent(jSeparator2)
                    .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(editarButon, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(añadirButon, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(eliminarButon, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(copiarButon, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(filtrarButon, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(refrescarButon, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(refrescarButon1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(refrescarButon2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 904, Short.MAX_VALUE)
                .addContainerGap())
        );

        paneles.addTab("General", jPanel2);

        graphicPanel.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        javax.swing.GroupLayout graphicPanelLayout = new javax.swing.GroupLayout(graphicPanel);
        graphicPanel.setLayout(graphicPanelLayout);
        graphicPanelLayout.setHorizontalGroup(
            graphicPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        graphicPanelLayout.setVerticalGroup(
            graphicPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 907, Short.MAX_VALUE)
        );

        butonPanelRight.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Others/lineGraphic.png"))); // NOI18N
        jButton1.setText("Línea");
        jButton1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton1.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        jButton1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Others/barGraphic.png"))); // NOI18N
        jButton2.setText("Barras");
        jButton2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton2.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        jButton2.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Others/areaChart.png"))); // NOI18N
        jButton3.setText("Area");
        jButton3.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton3.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        jButton3.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Others/defaultChart.png"))); // NOI18N
        jButton4.setText("Default");
        jButton4.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton4.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        jButton4.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jButton5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Others/lineGraphic.png"))); // NOI18N
        jButton5.setText("Línea 3d");
        jButton5.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton5.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        jButton5.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jButton6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Others/3dBarChart.png"))); // NOI18N
        jButton6.setText("Baras 3d");
        jButton6.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton6.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        jButton6.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout butonPanelRightLayout = new javax.swing.GroupLayout(butonPanelRight);
        butonPanelRight.setLayout(butonPanelRightLayout);
        butonPanelRightLayout.setHorizontalGroup(
            butonPanelRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(butonPanelRightLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(butonPanelRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton6, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        butonPanelRightLayout.setVerticalGroup(
            butonPanelRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(butonPanelRightLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(433, Short.MAX_VALUE))
        );

        butonPanelDown.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jSeparator3.setOrientation(javax.swing.SwingConstants.VERTICAL);

        botonesInformesToggleButon.add(saldoButon);
        saldoButon.setText("Saldo");
        saldoButon.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saldoButonActionPerformed(evt);
            }
        });

        botonesInformesToggleButon.add(ahorroButon);
        ahorroButon.setText("Ahorro");

        botonesInformesToggleButon.add(gastosButon);
        gastosButon.setText("Gastos");

        botonesInformesToggleButon.add(categoriaButon);
        categoriaButon.setText("Categoría");

        categoriasInformesComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        categoriasInformesComboBox.setEnabled(false);

        jButton9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Others/3dBarChart.png"))); // NOI18N
        jButton9.setText("Guardar");
        jButton9.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton9.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        jButton9.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
            }
        });

        jButton10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Others/3dBarChart.png"))); // NOI18N
        jButton10.setText("Cargar");
        jButton10.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton10.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        jButton10.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton10ActionPerformed(evt);
            }
        });

        jButton11.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Others/3dBarChart.png"))); // NOI18N
        jButton11.setText("Exportar");
        jButton11.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton11.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        jButton11.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton11ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout butonPanelDownLayout = new javax.swing.GroupLayout(butonPanelDown);
        butonPanelDown.setLayout(butonPanelDownLayout);
        butonPanelDownLayout.setHorizontalGroup(
            butonPanelDownLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(butonPanelDownLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(saldoButon, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ahorroButon, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(gastosButon, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(categoriaButon, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 8, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(categoriasInformesComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 455, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(662, 662, 662)
                .addComponent(jButton11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(117, 117, 117))
        );
        butonPanelDownLayout.setVerticalGroup(
            butonPanelDownLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, butonPanelDownLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(butonPanelDownLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jSeparator3)
                    .addComponent(categoriasInformesComboBox, javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(butonPanelDownLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(butonPanelDownLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jButton11, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton10, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton9, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(butonPanelDownLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(saldoButon, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(ahorroButon, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(gastosButon, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(categoriaButon, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap())
        );

        javax.swing.GroupLayout panel_InformesLayout = new javax.swing.GroupLayout(panel_Informes);
        panel_Informes.setLayout(panel_InformesLayout);
        panel_InformesLayout.setHorizontalGroup(
            panel_InformesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_InformesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel_InformesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(butonPanelDown, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(panel_InformesLayout.createSequentialGroup()
                        .addComponent(graphicPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(butonPanelRight, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        panel_InformesLayout.setVerticalGroup(
            panel_InformesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_InformesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel_InformesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(graphicPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(butonPanelRight, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(butonPanelDown, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        paneles.addTab("Evolución", panel_Informes);

        jPanel4.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1023, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jLabel1.setFont(new java.awt.Font("Georgia", 1, 100)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Mes - año");

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "INGRESOS", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 1, 18))); // NOI18N

        tablaIngresosCuentas.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Fecha", "Concepto", "Categoria", "Importe"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, true, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane4.setViewportView(tablaIngresosCuentas);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane4)
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 179, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "GASTOS", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 1, 18))); // NOI18N

        tablaGastosCuentas.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Fecha", "Concepto", "Categoria", "Importe"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(tablaGastosCuentas);

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1)
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 179, Short.MAX_VALUE)
                .addContainerGap())
        );

        jButton7.setText("->");

        jButton8.setText("<-");

        jPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "DESGLOSE CATEGORÍAS", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 1, 18))); // NOI18N

        tabalCategoriasCuentas.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Fecha", "Concepto", "Categoria", "Importe"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane5.setViewportView(tabalCategoriasCuentas);

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane5)
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 127, Short.MAX_VALUE)
                .addContainerGap())
        );

        jButton12.setText("jButton12");
        jButton12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton12ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addComponent(jButton8, javax.swing.GroupLayout.PREFERRED_SIZE, 278, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(84, 84, 84)
                        .addComponent(jButton12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 133, Short.MAX_VALUE)
                        .addComponent(jButton7, javax.swing.GroupLayout.PREFERRED_SIZE, 278, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel6, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel7, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jSeparator4))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSeparator4, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 111, Short.MAX_VALUE)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton7, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton8, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton12)))
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        paneles.addTab("Cuentas", jPanel3);

        ArchivoMenu.setText("Archivo");

        jMenuItem1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Others/loadExcel.png"))); // NOI18N
        jMenuItem1.setText("Cargar desde excel");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        ArchivoMenu.add(jMenuItem1);

        jMenuItem4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Others/historialLog.png"))); // NOI18N
        jMenuItem4.setText("Mostrar historial de registro");
        ArchivoMenu.add(jMenuItem4);

        jMenuItem2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Others/about.png"))); // NOI18N
        jMenuItem2.setText("Acerca de");
        ArchivoMenu.add(jMenuItem2);

        jMenuItem3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Others/settings.png"))); // NOI18N
        jMenuItem3.setText("Configuración");
        ArchivoMenu.add(jMenuItem3);

        jMenuBar1.add(ArchivoMenu);

        jMenu2.setText("Edit");
        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(paneles)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(paneles, javax.swing.GroupLayout.Alignment.TRAILING)
        );

        setSize(new java.awt.Dimension(1938, 1127));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        // TODO add your handling code here:
        // Control.leerExcel(archivo);
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void panelesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panelesMouseClicked
        // TODO add your handling code here:
        //this.createDefaultGraphic();
        /*
        if (!this.graficosYaIniciada) {
            //Es la primera vez que se abre este componente. Deben cargarse valores por defecto
            this.createDefaultGraphic();
            this.graficosYaIniciada = true;
        } else {
            this.setGraphic(actualData, "linea", "titulo", "Ejex", "ejey");
        }
         */
    }//GEN-LAST:event_panelesMouseClicked

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        JFreeChart defaultGraphic;
        DefaultCategoryDataset datos = new DefaultCategoryDataset();
        for (int i = Control.movimientos.size() - 1; i > 0; i = i - 1) {
            datos.addValue(Control.movimientos.get(i).getSaldo(), "Saldo cuenta", Control.movimientos.get(i).getFecha().toString());
        }
        defaultGraphic = ChartFactory.createLineChart("Evolucion de saldo", "Movimientos", "Cuantía", datos, PlotOrientation.VERTICAL, true, true, false);
        ChartPanel panel = new ChartPanel(defaultGraphic);
        this.graphicPanel.setLayout(new java.awt.BorderLayout());
        this.graphicPanel.add(panel);
        this.graphicPanel.validate();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        JFreeChart defaultGraphic;
        DefaultCategoryDataset datos = new DefaultCategoryDataset();
        for (int i = Control.movimientos.size() - 1; i > 0; i = i - 1) {
            datos.addValue(Control.movimientos.get(i).getSaldo(), "Saldo cuenta", Control.movimientos.get(i).getFecha().toString());
        }
        defaultGraphic = ChartFactory.createBarChart("Evolucion de saldo", "Movimientos", "Cuantía", datos, PlotOrientation.VERTICAL, true, true, false);
        ChartPanel panel = new ChartPanel(defaultGraphic);
        this.graphicPanel.setLayout(new java.awt.BorderLayout());
        this.graphicPanel.add(panel);
        this.graphicPanel.validate();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
        JFreeChart defaultGraphic;
        DefaultCategoryDataset datos = new DefaultCategoryDataset();
        for (int i = Control.movimientos.size() - 1; i > 0; i = i - 1) {
            datos.addValue(Control.movimientos.get(i).getSaldo(), "Saldo cuenta", Control.movimientos.get(i).getFecha().toString());
        }
        defaultGraphic = ChartFactory.createAreaChart("Evolucion de saldo", "Movimientos", "Cuantía", datos, PlotOrientation.VERTICAL, true, true, false);
        ChartPanel panel = new ChartPanel(defaultGraphic);
        this.graphicPanel.setLayout(new java.awt.BorderLayout());
        this.graphicPanel.add(panel);
        this.graphicPanel.validate();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        // TODO add your handling code here:
        //this.createStartGraphic();

    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        // TODO add your handling code here:
        JFreeChart defaultGraphic;
        DefaultCategoryDataset datos = new DefaultCategoryDataset();
        for (int i = Control.movimientos.size() - 1; i > 0; i = i - 1) {
            datos.addValue(Control.movimientos.get(i).getSaldo(), "Saldo cuenta", Control.movimientos.get(i).getFecha().toString());
        }
        defaultGraphic = ChartFactory.createLineChart3D("Evolucion de saldo", "Movimientos", "Cuantía", datos, PlotOrientation.VERTICAL, true, true, false);
        ChartPanel panel = new ChartPanel(defaultGraphic);
        this.graphicPanel.setLayout(new java.awt.BorderLayout());
        this.graphicPanel.add(panel);
        this.graphicPanel.validate();
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        // TODO add your handling code here:
        JFreeChart defaultGraphic;
        DefaultCategoryDataset datos = new DefaultCategoryDataset();
        for (int i = Control.movimientos.size() - 1; i > 0; i = i - 1) {
            datos.addValue(Control.movimientos.get(i).getSaldo(), "Saldo cuenta", Control.movimientos.get(i).getFecha().toString());
        }
        defaultGraphic = ChartFactory.createStackedBarChart3D("Evolucion de saldo", "Movimientos", "Cuantía", datos, PlotOrientation.VERTICAL, true, true, false);
        ChartPanel panel = new ChartPanel(defaultGraphic);
        this.graphicPanel.setLayout(new java.awt.BorderLayout());
        this.graphicPanel.add(panel);
        this.graphicPanel.validate();
    }//GEN-LAST:event_jButton6ActionPerformed

    private void panelesComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_panelesComponentShown
        // TODO add your handling code here:
        //this.createStartGraphic();

    }//GEN-LAST:event_panelesComponentShown

    private void añadirButonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_añadirButonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_añadirButonActionPerformed

    private void saldoButonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saldoButonActionPerformed
        // TODO add your handling code here:
        DefaultCategoryDataset datos = new DefaultCategoryDataset();
        for (int i = Control.movimientos.size() - 1; i > 0; i = i - 1) {
            datos.addValue(Control.movimientos.get(i).getSaldo(), "Saldo cuenta", Control.movimientos.get(i).getFecha().toString());
        }
        this.actualData = datos;
        this.setGraphic(actualData, "linea", "Evolución de saldo", "Movimientos", "Saldo");
    }//GEN-LAST:event_saldoButonActionPerformed

    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton9ActionPerformed

    private void jButton10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton10ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton10ActionPerformed

    private void jButton11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton11ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton11ActionPerformed

    private void jButton12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton12ActionPerformed
        // TODO add your handling code here:
        GregorianCalendar instanteActual = new GregorianCalendar();
        this.mesActual = instanteActual.get(GregorianCalendar.MONTH) - 1;
        this.actualYear = instanteActual.get(GregorianCalendar.YEAR) - 1;
        this.extraerMovimientosMes(this.mesActual, this.actualYear);
        this.escribirTablaIngresos();
    }//GEN-LAST:event_jButton12ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */

        try {

            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }

            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        try {
            UIManager.setLookAndFeel(
                    //UIManager.getCrossPlatformLookAndFeelClassName()
                    UIManager.getSystemLookAndFeelClassName()
            //com.sun.java.swing.plaf.gtk.GTKLookAndFeel
            //com.sun.java.swing.plaf.windows.WindowsLookAndFeel
            );
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    new Main().setVisible(true);
                } catch (IOException ex) {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ParseException ex) {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenu ArchivoMenu;
    private javax.swing.JToggleButton ahorroButon;
    private javax.swing.JButton añadirButon;
    private javax.swing.ButtonGroup botonesInformesToggleButon;
    private javax.swing.JPanel butonPanelDown;
    private javax.swing.JPanel butonPanelRight;
    private javax.swing.JToggleButton categoriaButon;
    private javax.swing.JComboBox<String> categoriasInformesComboBox;
    private javax.swing.JButton copiarButon;
    private javax.swing.JButton editarButon;
    private javax.swing.JButton eliminarButon;
    private javax.swing.JButton filtrarButon;
    private javax.swing.JToggleButton gastosButon;
    private javax.swing.JPanel graphicPanel;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton11;
    private javax.swing.JButton jButton12;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JPanel panel_Informes;
    private javax.swing.JTabbedPane paneles;
    private javax.swing.JButton refrescarButon;
    private javax.swing.JButton refrescarButon1;
    private javax.swing.JButton refrescarButon2;
    private javax.swing.JToggleButton saldoButon;
    private javax.swing.JTable tabalCategoriasCuentas;
    public javax.swing.JTable tabla;
    private javax.swing.JTable tablaGastosCuentas;
    private javax.swing.JTable tablaIngresosCuentas;
    private javax.swing.JTextPane texto;
    // End of variables declaration//GEN-END:variables
}
