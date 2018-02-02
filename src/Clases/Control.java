/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Clases;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import javax.swing.JOptionPane;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author Urbano
 */
public class Control {

    public static ArrayList<Movimiento> movimientos = new ArrayList<>();
    public static HashMap<String, ArrayList<String>> categorias = new HashMap<>();
    public static Properties config = new Properties();
    public static InputStream configInput = null;
    public static OutputStream configOutput = null;

    public static void leerExcel(File archivo) throws IOException, ParseException {
        String nombre = archivo.getName();
        String extension = nombre.substring(nombre.lastIndexOf(".") + 1);
        switch (extension) {
            case "xls":
                leerXls(archivo);
                break;
            case "xlsx":
                leerXlsx(archivo);
                break;
            default:
                //La extensión del archivo no es correcta
                JOptionPane.showMessageDialog(null, "Extensión de archvio incorrecta", "Error de control", JOptionPane.ERROR_MESSAGE);
                break;
        }
    }

    public static void leerXls(File archivo) throws FileNotFoundException, IOException, ParseException {
        String nombreArchivo = archivo.getName();
        List dataSheet = new ArrayList(); //ArrayList para guardar los datos recogidos del excel
        FileInputStream fis = null;

        try {
            fis = new FileInputStream(nombreArchivo);
            HSSFWorkbook wb = new HSSFWorkbook(fis); //Creamos el WorkBook
            HSSFSheet sheet = wb.getSheetAt(0); //Creamos el prier SHEET (hoja) del WorkBook

            /*
            Cuando tenemos el objeto hoja (sheet) creamos un iterador para recorrerlo.
            Cada hoja tiene filas y cada fila tiene columnas. Guardamos los datos leídos
            en el arrayList (dataSheet) y podemos imprimir entonces el contenido del
            en la consola
             */
            Iterator filas = sheet.rowIterator();
            int contadorFilas = 0;
            while (filas.hasNext()) {
                //Recorremos por filas. Con un switch le decimos que hacer
                contadorFilas++;
                HSSFRow fila = (HSSFRow) filas.next();
                Iterator celdas = fila.cellIterator();
                //List data = new ArrayList();
                if (contadorFilas > 11) { //Cuando estamos en la fila 12 ya empezamos a contar columnas
                    Date fechaOp = null;
                    String fechaValor = null;
                    String concepto = null;
                    Double importe = null;
                    Double saldo = null;
                    int contadorColumnas = 0;
                    while (celdas.hasNext()) {
                        contadorColumnas++;
                        HSSFCell celda = (HSSFCell) celdas.next();
                        //data.add(celda);
                        switch (contadorColumnas) {
                            case 2: //estamos en columna fecha operación
                                if (celda.toString() != null) {
                                    fechaOp = celda.getDateCellValue();
                                }
                                break;
                            case 4:
                                if (celda.toString() != null) {
                                    fechaValor = celda.toString();
                                }
                                break;
                            case 6:
                                if (celda.toString() != null) {
                                    concepto = celda.toString();
                                }
                                break;
                            case 8:
                                if (!celda.toString().isEmpty()) {
                                    importe = Double.parseDouble(celda.toString());
                                }
                                break;
                            case 10:
                                if (!celda.toString().isEmpty()) {
                                    saldo = Double.parseDouble(celda.toString());
                                }
                                break;
                        }
                        if (fechaOp != null && fechaValor != null && concepto != null && importe != null && saldo != null) {
                            movimientos.add(new Movimiento(fechaOp, concepto, importe, saldo));
                        }
                    }
                }
            }
            movimientos.remove(0);
        } catch (IOException er) {
            JOptionPane.showMessageDialog(null, er.getMessage(), "Error de control", JOptionPane.ERROR_MESSAGE);
        } finally {
            if (fis != null) {
                fis.close();
            }
        }
        // showExcelData(dataSheet);
    }

    public static void showExcelData(List sheetData) {
        //Itera los datos y los saca por consola
        for (int i = 0; i < sheetData.size(); i++) {
            List list = (List) sheetData.get(i);
            for (int j = 0; j < list.size(); j++) {
                Cell cell = (Cell) list.get(j);
                if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
                    System.out.println(cell.getNumericCellValue());
                } else {
                    if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
                        System.out.println(cell.getRichStringCellValue());
                    } else {
                        if (cell.getCellType() == Cell.CELL_TYPE_BOOLEAN) {
                            System.out.println(cell.getBooleanCellValue());
                        }
                        if (j < list.size() - 1) {
                            System.out.println(", ");
                        }
                    }
                    System.out.println("-");
                }
            }
        }
    }

    public static void leerXlsx(File archivo) throws ParseException, FileNotFoundException, IOException {
        String nombreArchivo = archivo.getName();
        List dataSheet = new ArrayList(); //ArrayList para guardar los datos recogidos del excel
        FileInputStream fis = null;

        try {
            fis = new FileInputStream(nombreArchivo);
            XSSFWorkbook wb = new XSSFWorkbook(fis); //Creamos el WorkBook
            XSSFSheet sheet = wb.getSheetAt(0); //Creamos el prier SHEET (hoja) del WorkBook

            /*
            Cuando tenemos el objeto hoja (sheet) creamos un iterador para recorrerlo.
            Cada hoja tiene filas y cada fila tiene columnas. Guardamos los datos leídos
            en el arrayList (dataSheet) y podemos imprimir entonces el contenido del
            en la consola
             */
            Iterator filas = sheet.rowIterator();
            while (filas.hasNext()) {
                XSSFRow fila = (XSSFRow) filas.next();

                Iterator celdas = fila.cellIterator();
                List data = new ArrayList();
                while (celdas.hasNext()) {
                    XSSFCell celda = (XSSFCell) celdas.next();
                    System.out.println("Añadiendo celda: " + celda.toString());
                    data.add(celda);
                }
                dataSheet.add(data);
            }
        } catch (IOException er) {
            System.out.println(er.getMessage());
        } finally {
            if (fis != null) {
                fis.close();
            }
        }
    }

    public static Date stringToDate(String cadena) throws ParseException {
        SimpleDateFormat formato = new SimpleDateFormat("dd-MM-yyyy");
        Date fecha = null;
        try {
            fecha = formato.parse(cadena);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Error de control", JOptionPane.ERROR_MESSAGE);
        }
        return fecha;
    }

    public static String fechaToString(Date fecha) {
        String cadena = null;
        SimpleDateFormat formato = new SimpleDateFormat("dd-MM-yyyy");
        try {
            cadena = formato.format(fecha);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Error de control", JOptionPane.ERROR_MESSAGE);
        }
        return cadena;
    }

    public static void loadDefaultCategories() throws FileNotFoundException, IOException {
        
        try {
            //Cargamos categoría NT
            FileReader fr = new FileReader("data/nt.dat");
            BufferedReader br = new BufferedReader(fr);
            StringTokenizer tkn = new StringTokenizer(br.readLine(), ".");
            ArrayList etiquetas = new ArrayList();
            while (tkn.hasMoreTokens()) {
                String etiqueta = tkn.nextToken();
                etiquetas.add(etiqueta);
            }
            categorias.put("Nominas y trabajo", etiquetas);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error en control: cargarCategorias().NT\n" + e.getMessage(), "Error de control", JOptionPane.ERROR_MESSAGE);
        }
        
        try {
            //Cargamos categoría HFS
            FileReader fr = new FileReader("data/hfs.dat");
            BufferedReader br = new BufferedReader(fr);
            StringTokenizer tkn = new StringTokenizer(br.readLine(), ".");
            ArrayList etiquetas = new ArrayList();
            while (tkn.hasMoreTokens()) {
                String etiqueta = tkn.nextToken();
                etiquetas.add(etiqueta);
            }
            categorias.put("Hogar, familia y suministros", etiquetas);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error en control: cargarCategorias().HFS\n" + e.getMessage(), "Error de control", JOptionPane.ERROR_MESSAGE);
        }
        try {
            //Cargamos categoría CCO
            FileReader fr = new FileReader("data/cco.dat");
            BufferedReader br = new BufferedReader(fr);
            StringTokenizer tkn = new StringTokenizer(br.readLine(), ".");
            ArrayList etiquetas = new ArrayList();
            while (tkn.hasMoreTokens()) {
                String etiqueta = tkn.nextToken();
                etiquetas.add(etiqueta);
            }
            categorias.put("Comercio y compras online", etiquetas);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error en control: cargarCategorias().CCO\n" + e.getMessage(), "Error de control", JOptionPane.ERROR_MESSAGE);
        }

        try {
            //Cargamos categoría FUC
            FileReader fr = new FileReader("data/fuc.dat");
            BufferedReader br = new BufferedReader(fr);
            StringTokenizer tkn = new StringTokenizer(br.readLine(), ".");
            ArrayList etiquetas = new ArrayList();
            while (tkn.hasMoreTokens()) {
                String etiqueta = tkn.nextToken();
                etiquetas.add(etiqueta);
            }
            categorias.put("Formacion, universidades y colegios", etiquetas);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error en control: cargarCategorias().FUC\n" + e.getMessage(), "Error de control", JOptionPane.ERROR_MESSAGE);
        }

        try {
            //Cargamos categoría ISBOO
            FileReader fr = new FileReader("data/isboo.dat");
            BufferedReader br = new BufferedReader(fr);
            StringTokenizer tkn = new StringTokenizer(br.readLine(), ".");
            ArrayList etiquetas = new ArrayList();
            while (tkn.hasMoreTokens()) {
                String etiqueta = tkn.nextToken();
                etiquetas.add(etiqueta);
            }
            categorias.put("Impuestos, seguros, bancos y otros organismos", etiquetas);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error en control: cargarCategorias().ISBOO\n" + e.getMessage(), "Error de control", JOptionPane.ERROR_MESSAGE);
        }

        try {
            //Cargamos categoría OCT
            FileReader fr = new FileReader("data/oct.dat");
            BufferedReader br = new BufferedReader(fr);
            StringTokenizer tkn = new StringTokenizer(br.readLine(), ".");
            ArrayList etiquetas = new ArrayList();
            while (tkn.hasMoreTokens()) {
                String etiqueta = tkn.nextToken();
                etiquetas.add(etiqueta);
            }
            categorias.put("Otros, cajeros y transferencias", etiquetas);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error en control: cargarCategorias().OCT\n" + e.getMessage(), "Error de control", JOptionPane.ERROR_MESSAGE);
        }

        try {
            //Cargamos categoría ORVV
            FileReader fr = new FileReader("data/orvv.dat");
            BufferedReader br = new BufferedReader(fr);
            StringTokenizer tkn = new StringTokenizer(br.readLine(), ".");
            ArrayList etiquetas = new ArrayList();
            while (tkn.hasMoreTokens()) {
                String etiqueta = tkn.nextToken();
                etiquetas.add(etiqueta);
            }
            categorias.put("Ocio, restauracion, viajes y vacaciones", etiquetas);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error en control: cargarCategorias().ORVV\n" + e.getMessage(), "Error de control", JOptionPane.ERROR_MESSAGE);
        }

        try {
            //Cargamos categoría SBB
            FileReader fr = new FileReader("data/sbb.dat");
            BufferedReader br = new BufferedReader(fr);
            StringTokenizer tkn = new StringTokenizer(br.readLine(), ".");
            ArrayList etiquetas = new ArrayList();
            while (tkn.hasMoreTokens()) {
                String etiqueta = tkn.nextToken();
                etiquetas.add(etiqueta);
            }
            categorias.put("Salud, belleza y bienestar", etiquetas);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error en control: cargarCategorias().SBB\n" + e.getMessage(), "Error de control", JOptionPane.ERROR_MESSAGE);
        }

        try {
            //Cargamos categoría VT
            FileReader fr = new FileReader("data/vt.dat");
            BufferedReader br = new BufferedReader(fr);
            StringTokenizer tkn = new StringTokenizer(br.readLine(), ".");
            ArrayList etiquetas = new ArrayList();
            while (tkn.hasMoreTokens()) {
                String etiqueta = tkn.nextToken();
                etiquetas.add(etiqueta);
            }
            categorias.put("Vehiculo y transporte", etiquetas);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error en control: cargarCategorias().VT\n" + e.getMessage(), "Error de control", JOptionPane.ERROR_MESSAGE);
        }
        saveCatgories();
    }

    public static void saveCatgories() throws FileNotFoundException {
        try {
            FileOutputStream outputStream = new FileOutputStream("data/cat.dat");
            ObjectOutputStream output = new ObjectOutputStream(outputStream);
            output.writeObject(categorias);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error guardando categorias:\n" + e.getMessage(), "Error de control", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void loadCategories() {
        try {
            FileInputStream stream = new FileInputStream("data/cat.dat");
            ObjectInputStream input = new ObjectInputStream(stream);
            categorias = (HashMap<String, ArrayList<String>>) input.readObject();
            JOptionPane.showMessageDialog(null, "Archivos cargados con éxito", "Olé", JOptionPane.INFORMATION_MESSAGE);
            //System.out.println(categorias);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al cargar categorías.\n" + e.getMessage(), "Error de control", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public static void loadConfig(){
        try{
            configInput = new FileInputStream("data/config.properties");
            config.load(configInput);
            System.out.println(config.getProperty("first_start"));
            System.out.println(config.getProperty("look_and_feel"));
        } catch(Exception e){
            JOptionPane.showMessageDialog(null, "Error cargando configuración\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public static void setPropertyValue(String property, String value){
        try{
            configOutput = new FileOutputStream("data/config.properties");
            config.setProperty(property, value);
        } catch(Exception e){
            JOptionPane.showMessageDialog(null, "Error guardando configuración\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public static void categorize() {
        try {
            String categoria = null;
            String etiqueta = null;
            String concepto = null;
            Iterator it = categorias.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry e = (Map.Entry) it.next();
                //e es un objeto de hashmap. Podemos solicitar su clave y su valor
                categoria = e.getKey().toString();
                ArrayList<String> listaEtiquetas = (ArrayList<String>) e.getValue();
                for (int mov = 0; mov < movimientos.size(); mov++) {
                    for (int pClave = 0; pClave < listaEtiquetas.size(); pClave++) {
                        concepto = movimientos.get(mov).getConcepto();
                        etiqueta = categorias.get(categoria).get(pClave);
                        //System.out.println(concepto + " -- " + etiqueta);
                        //Primer nivel: identificar ingresos de trabajo:
//                        if(!concepto.isEmpty()){
//                            ArrayList<String> cat = categorias.get("Nominas y trabajo");
//                            for(int et = 0; et < cat.size(); et++){
//                                if(concepto.toUpperCase().contains(cat.get(et)));
//                                movimientos.get(mov).setCategoria("Nominas y trabajo");
//                            }
//                        }
                        if (!concepto.isEmpty()) {
                            if (concepto.toUpperCase().contains(etiqueta.toUpperCase()) && movimientos.get(mov).getCategoria().isEmpty()) {
                                movimientos.get(mov).setCategoria(categoria);
                                //System.out.println("El concepto " + concepto + " pertenece a " + categoria);
                                break;
                            }
                        }
                    }
                }
                //System.out.println(e.getKey().toString() + " " + e.getValue());
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al categorizar.\n" + e.getMessage(), "Error de control", JOptionPane.ERROR_MESSAGE);
        }
    }
}
