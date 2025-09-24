package co.vinni.kafka.SBConsumidor.iu;

import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import javax.swing.*;
import java.awt.*;
import java.time.Duration;
import java.util.*;
import java.util.List;

public class ConsumidorRopaUI extends JFrame {
    private JComboBox<String> comboCategoria;
    private DefaultListModel<String> listaModel;
    private JList<String> listaProductos;
    private JButton btnFactura;
    private JTextArea areaFactura;

    private KafkaConsumer<String, String> consumer;
    private List<String> productosRecibidos; // almacena todos los productos con su topic

    public ConsumidorRopaUI() {
        setTitle("Consumidor de Ropa (con filtro por categoría)");
        setSize(600, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // --- TOP: Combo categorías ---
        comboCategoria = new JComboBox<>(new String[]{"camisas", "pantalones", "zapatos"});
        JPanel top = new JPanel();
        top.add(new JLabel("Categoría:"));
        top.add(comboCategoria);
        add(top, BorderLayout.NORTH);

        // --- CENTRO: Lista productos ---
        listaModel = new DefaultListModel<>();
        listaProductos = new JList<>(listaModel);
        add(new JScrollPane(listaProductos), BorderLayout.CENTER);

        // --- SUR: Factura ---
        btnFactura = new JButton("Total");
        areaFactura = new JTextArea();

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.add(btnFactura, BorderLayout.NORTH);
        bottom.add(new JScrollPane(areaFactura), BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);

        productosRecibidos = new ArrayList<>();

        configurarKafka();
        escucharProductos();

        // Filtrar cada vez que se cambie la categoría
        comboCategoria.addActionListener(e -> filtrarProductos());

        btnFactura.addActionListener(e -> generarFactura());
    }

    private void configurarKafka() {
        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("group.id", "grupo-multi");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        consumer = new KafkaConsumer<>(props);

        // Suscribirse a todos los topics
        consumer.subscribe(Arrays.asList("camisas", "pantalones", "zapatos"));
    }

    private void escucharProductos() {
        new Thread(() -> {
            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));
                records.forEach(record -> {
                    String mensaje = "[" + record.topic() + "] " + record.value();
                    productosRecibidos.add(mensaje);
                    filtrarProductos(); // actualizar vista según filtro activo
                });
            }
        }).start();
    }

    private void filtrarProductos() {
        String categoriaSeleccionada = (String) comboCategoria.getSelectedItem();
        listaModel.clear();
        for (String prod : productosRecibidos) {
            if (prod.startsWith("[" + categoriaSeleccionada + "]")) {
                listaModel.addElement(prod);
            }
        }
    }

    private void generarFactura() {
        List<String> seleccionados = listaProductos.getSelectedValuesList();
        if (seleccionados.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No seleccionaste productos.");
            return;
        }

        StringBuilder factura = new StringBuilder("Factura del Cliente:\n");
        double total = 0;

        for (String producto : seleccionados) {
            factura.append(producto).append("\n");
            try {
                // Extraer precio si está en formato "... - $precio"
                String[] partes = producto.split("\\$");
                total += Double.parseDouble(partes[1]);
            } catch (Exception e) {
                // Ignorar si no hay precio válido
            }
        }
        factura.append("Total: $").append(total);

        areaFactura.setText(factura.toString());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ConsumidorRopaUI().setVisible(true));
    }
}
