
package afd;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.geom.QuadCurve2D;
import java.util.ArrayList;
import java.util.List;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

// Clase para representar un Estado
class Estado {
    String nombre;
    boolean esInicial;
    boolean esAceptacion;
                                                                                                                    

    public Estado(String nombre, boolean esInicial, boolean esAceptacion) {
        this.nombre = nombre;
        this.esInicial = esInicial;
        this.esAceptacion = esAceptacion;
    }
}

// Clase para representar una Transición
class Transicion {
    Estado desde;
    Estado hacia;
    char simbolo;

    public Transicion(Estado desde, Estado hacia, char simbolo) {
        this.desde = desde;
        this.hacia = hacia;
        this.simbolo = simbolo;
    }
}

// Clase para representar un Autómata
class Automata {
    List<Estado> estados;
    List<Transicion> transiciones;

    public Automata() {
        this.estados = new ArrayList<>();
        this.transiciones = new ArrayList<>();
    }

    public void agregarEstado(Estado estado) {
        estados.add(estado);
    }

    public void agregarTransicion(Transicion transicion) {
        transiciones.add(transicion);
    }

    public boolean verificarCadenaAFD(String cadena, Estado estadoInicial, Estado estadoFinal, String[] alfabeto) {
        Estado actual = estadoInicial;

        for (char simbolo : cadena.toCharArray()) {
            boolean simboloValido = false;
            for (String s : alfabeto) {
                if (s.equals(Character.toString(simbolo))) {
                    simboloValido = true;
                    break;
                }
            }

            if (!simboloValido) {
                return false;
            }

            boolean transicionValida = false;
            for (Transicion t : transiciones) {
                if (t.desde == actual && t.simbolo == simbolo) {
                    actual = t.hacia;
                    transicionValida = true;
                    break;
                }
            }
            if (!transicionValida) {
                return false;
            }
        }
        return actual.equals(estadoFinal);
    }

    public boolean verificarCadenaAFND(String cadena, Estado estadoInicial, Estado estadoFinal, String[] alfabeto) {
        return verificarCadenaAFNDRecursivo(cadena, estadoInicial, estadoFinal, 0, alfabeto);
    }

    private boolean verificarCadenaAFNDRecursivo(String cadena, Estado actual, Estado estadoFinal, int indice, String[] alfabeto) {
        if (indice == cadena.length()) {
            return actual.equals(estadoFinal);
        }

        char simbolo = cadena.charAt(indice);

        boolean simboloValido = false;
        for (String s : alfabeto) {
            if (s.equals(Character.toString(simbolo))) {
                simboloValido = true;
                break;
            }
        }

        if (!simboloValido) {
            return false;
        }

        List<Estado> posiblesEstados = new ArrayList<>();
        for (Transicion t : transiciones) {
            if (t.desde == actual && t.simbolo == simbolo) {
                posiblesEstados.add(t.hacia);
            }
        }

        for (Estado estado : posiblesEstados) {
            if (verificarCadenaAFNDRecursivo(cadena, estado, estadoFinal, indice + 1, alfabeto)) {
                return true;
            }
        }
        return false;
    }
}

// Clase para la interfaz gráfica del autómata
class VentanaAutomata extends JFrame {
    private Automata automata;
    private JTextField txtAlfabeto, txtEstados, txtEstadoInicial, txtEstadoFinal, txtCadena;
    private JTextField txtResultado, txtEstadoFinalCadena;
    private JTable tablaTransiciones;
    private DefaultTableModel modeloTabla;
    private JButton btnGenerarImagen;
    private JRadioButton rbAFD, rbAFND;

    public VentanaAutomata(Automata automata) {
        this.automata = automata;
        inicializarComponentes();
    }

    private void inicializarComponentes() {
        setTitle("Evaluador AFD/AFND");
        setSize(900, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Cambiar el color de fondo de la ventana principal
        getContentPane().setBackground(Color.WHITE);

        JPanel panelDatos = new JPanel();
        panelDatos.setLayout(new GridLayout(6, 2));
        panelDatos.setBackground(Color.LIGHT_GRAY); // Color de fondo para el panel de datos

        panelDatos.add(new JLabel("Alfabeto (separado por comas):"));
        txtAlfabeto = new JTextField();
        panelDatos.add(txtAlfabeto);

        panelDatos.add(new JLabel("Estados (separados por comas):"));
        txtEstados = new JTextField();
        panelDatos.add(txtEstados);

        panelDatos.add(new JLabel("Estado Inicial:"));
        txtEstadoInicial = new JTextField();
        panelDatos.add(txtEstadoInicial);

        panelDatos.add(new JLabel("Estado Final:"));
        txtEstadoFinal = new JTextField();
        panelDatos.add(txtEstadoFinal);

        panelDatos.add(new JLabel("Tipo de Autómata:"));
        JPanel panelTipo = new JPanel();
        panelTipo.setBackground(Color.LIGHT_GRAY); // Color de fondo para el panel de tipo
        rbAFD = new JRadioButton("AFD");
        rbAFND = new JRadioButton("AFND");
        ButtonGroup grupo = new ButtonGroup();
        grupo.add(rbAFD);
        grupo.add(rbAFND);
        rbAFD.setSelected(true);
        panelTipo.add(rbAFD);
        panelTipo.add(rbAFND);
        panelDatos.add(panelTipo);

        JButton btnGenerarTabla = new JButton("Generar Tabla de Transiciones");
        panelDatos.add(btnGenerarTabla);

        add(panelDatos, BorderLayout.WEST);

        JPanel panelEvaluacion = new JPanel();
        panelEvaluacion.setLayout(new GridLayout(5, 2));
        panelEvaluacion.setBackground(Color.WHITE); // Color de fondo para el panel de evaluación

        panelEvaluacion.add(new JLabel("Cadena a evaluar (sin comas):"));
        txtCadena = new JTextField();
        panelEvaluacion.add(txtCadena);

        panelEvaluacion.add(new JLabel("Resultado:"));
        txtResultado = new JTextField();
        txtResultado.setEditable(false);
        panelEvaluacion.add(txtResultado);

        panelEvaluacion.add(new JLabel("Estado final de la cadena ingresada:"));
        txtEstadoFinalCadena = new JTextField();
        txtEstadoFinalCadena.setEditable(false);
        panelEvaluacion.add(txtEstadoFinalCadena);

        JButton btnEvaluar = new JButton("Evaluar");
        panelEvaluacion.add(btnEvaluar);

        add(panelEvaluacion, BorderLayout.EAST);

        JPanel panelTransiciones = new JPanel();
        panelTransiciones.setLayout(new BorderLayout());

        modeloTabla = new DefaultTableModel();
        tablaTransiciones = new JTable(modeloTabla);
        tablaTransiciones.setBackground(Color.LIGHT_GRAY); // Color de fondo para la tabla de transiciones
        panelTransiciones.add(new JScrollPane(tablaTransiciones), BorderLayout.CENTER);

        add(panelTransiciones, BorderLayout.CENTER);

        btnGenerarImagen = new JButton("Generar Imagen del Autómata");
        btnGenerarImagen.setEnabled(false);
        add(btnGenerarImagen, BorderLayout.SOUTH);

        btnGenerarTabla.addActionListener(e -> {
            try {
                generarTablaTransiciones();
                btnGenerarImagen.setEnabled(true);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al generar la tabla de transiciones: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnEvaluar.addActionListener(e -> {
            try {
                agregarTransiciones();
                evaluarCadena();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al evaluar la cadena: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnGenerarImagen.addActionListener(e -> {
            try {
                generarImagenAutomata();
                generarImagenAutomata();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al generar la imagen: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private void generarTablaTransiciones() throws Exception {
        modeloTabla.setRowCount(0);
        modeloTabla.setColumnCount(0);

        String[] alfabeto = txtAlfabeto.getText().split(",");
        String[] estados = txtEstados.getText().split(",");
        if (alfabeto.length == 0 || estados.length == 0) {
            throw new Exception("Debe ingresar el alfabeto y los estados.");
        }

        automata.estados.clear();
        automata.transiciones.clear();

        modeloTabla.addColumn("Estado");
        for (String simbolo : alfabeto) {
            modeloTabla.addColumn(simbolo.trim());
        }

        Estado estadoFinal = null; // Variable para almacenar el estado final
        for (String estado : estados) {
            Estado nuevoEstado = new Estado(estado.trim(), false, false);
            automata.agregarEstado(nuevoEstado);
            Object[] fila = new Object[alfabeto.length + 1];
            fila[0] = estado.trim();
            for (int i = 1; i <= alfabeto.length; i++) {
                fila[i] = "";
            }
            modeloTabla.addRow(fila);

            // Marcar el estado final como de aceptación
            if (estado.trim().equals(estados[estados.length - 1].trim())) {
                nuevoEstado.esAceptacion = true; // Marcar como estado de aceptación
                estadoFinal = nuevoEstado; // Guardar referencia al estado final
            }
        }


        txtEstadoInicial.setText(estados[0].trim());
        txtEstadoFinal.setText(estados[estados.length - 1].trim());

        btnGenerarImagen.setEnabled(true);
    }

    private void agregarTransiciones() {
        for (int i = 0; i < modeloTabla.getRowCount(); i++) {
            for (int j = 1; j < modeloTabla.getColumnCount(); j++) {
                String simbolo = modeloTabla.getColumnName(j);
                String estadoDestino = (String) modeloTabla.getValueAt(i, j);
                if (!estadoDestino.isEmpty()) {
                    Estado desde = automata.estados.get(i);
                    Estado hacia = buscarEstadoPorNombre(estadoDestino);
                    if (hacia != null) {
                        automata.agregarTransicion(new Transicion(desde, hacia, simbolo.charAt(0)));
                    }
                }
            }
        }
    }

    private Estado buscarEstadoPorNombre(String nombre) {
        for (Estado estado : automata.estados) {
            if (estado.nombre.equals(nombre)) {
                return estado;
            }
        }
        return null;
    }

    private void evaluarCadena() {
        String cadena = txtCadena.getText().trim();
        Estado estadoInicial = buscarEstadoPorNombre(txtEstadoInicial.getText().trim());
        Estado estadoFinal = buscarEstadoPorNombre(txtEstadoFinal.getText().trim());

        boolean resultado = false;
        if (rbAFD.isSelected()) {
            resultado = automata.verificarCadenaAFD(cadena, estadoInicial, estadoFinal, txtAlfabeto.getText().split(","));
        } else if (rbAFND.isSelected()) {
            resultado = automata.verificarCadenaAFND(cadena, estadoInicial, estadoFinal, txtAlfabeto.getText().split(","));
        }

        txtResultado.setText(resultado ? "Cadena aceptada" : "Cadena no aceptada");
        txtEstadoFinalCadena.setText(estadoFinal.nombre);
    }

private void generarImagenAutomata() throws Exception {
    int ancho = 1600;
    int alto = 1200;
    BufferedImage imagen = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_RGB);
    Graphics2D g = imagen.createGraphics();
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g.setColor(Color.WHITE);
    g.fillRect(0, 0, ancho, alto);
    g.setColor(Color.BLACK);
    g.setFont(new Font("Arial", Font.BOLD, 25)); // Aumentar el tamaño de la fuente
    g.drawString("Autómata", 700, 30);

    // Dibuja los estados
    int radio = 200; // Duplicar el radio
    int margenX = 100;
    int margenY = 100;
    int separacionX = 300; // Aumentar la separación
    int separacionY = 300; // Aumentar la separación

    for (int i = 0; i < automata.estados.size(); i++) {
        Estado estado = automata.estados.get(i);
        int x = margenX + (i % 5) * separacionX;
        int y = margenY + (i / 5) * separacionY;

        // Cambiar color de fondo de los estados
        g.setColor(estado.esAceptacion ? Color.GREEN : Color.GRAY);
        g.fillOval(x, y, radio, radio);

        // Dibujar el contorno negro del estado
        g.setColor(Color.BLACK);
        g.drawOval(x, y, radio, radio);

        // Doble círculo para estados de aceptación
        if (estado.esAceptacion) {
            g.setColor(Color.BLACK);
            g.drawOval(x - 20, y - 20, radio + 40, radio + 40); // Aumentar el tamaño del doble círculo
        }

        // Dibujar el nombre del estado
        g.setColor(Color.BLACK);
        g.drawString(estado.nombre, x + radio / 2 - 30, y + radio / 2 + 10); // Ajustar posición del texto
    }

    // Dibuja las transiciones
    for (Transicion transicion : automata.transiciones) {
        int indiceDesde = automata.estados.indexOf(transicion.desde);
        int indiceHacia = automata.estados.indexOf(transicion.hacia);

        int xDesde = margenX + (indiceDesde % 5) * separacionX + radio / 2;
        int yDesde = margenY + (indiceDesde / 5) * separacionY + radio / 2;

        int xHacia = margenX + (indiceHacia % 5) * separacionX + radio / 2;
        int yHacia = margenY + (indiceHacia / 5) * separacionY + radio / 2;

        // Dibujar la transición como una curva
        g.setColor(Color.BLUE);
        g.setStroke(new BasicStroke(6)); // Aumentar el grosor de la línea
        QuadCurve2D curva = new QuadCurve2D.Float(xDesde, yDesde, (xDesde + xHacia) / 2, yDesde - 100, xHacia, yHacia);
        g.draw(curva);

        // Dibujar el símbolo de la transición en el centro de la curva
        g.setColor(Color.BLACK);
        int textoX = (xDesde + xHacia) / 2;
        int textoY = (yDesde + yHacia) / 2 - 50; // Ajustar la posición del texto
        g.setFont(new Font("Arial", Font.BOLD, 24)); // Aumentar el tamaño de la fuente
        g.drawString(String.valueOf(transicion.simbolo), textoX, textoY);
    }

    g.dispose();
    ImageIO.write(imagen, "png", new File("automata.png"));
    JOptionPane.showMessageDialog(this, "Imagen del autómata generada como automata.png");
}



// Clase principal
public class ADF {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Automata automata = new Automata();
            VentanaAutomata ventana = new VentanaAutomata(automata);
            ventana.setVisible(true);
        });
    }
}
}
