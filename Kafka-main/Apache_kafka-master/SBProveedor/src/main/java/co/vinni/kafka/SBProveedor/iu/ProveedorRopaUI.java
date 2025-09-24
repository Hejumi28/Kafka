package co.vinni.kafka.SBProveedor.iu;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import javax.swing.*;
import java.awt.*;
import java.util.Properties;

public class ProveedorRopaUI extends JFrame {
    private JTextField txtNombre;
    private JTextField txtPrecio;
    private JComboBox<String> comboCategoria;
    private JButton btnEnviar;

    private KafkaProducer<String, String> producer;

    public ProveedorRopaUI() {
        setTitle("Proveedor de Ropa");
        setSize(400, 200);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new GridLayout(4, 2));

        txtNombre = new JTextField();
        txtPrecio = new JTextField();
        comboCategoria = new JComboBox<>(new String[]{"camisas", "pantalones", "zapatos"});
        btnEnviar = new JButton("Enviar Producto");

        add(new JLabel("Nombre Producto:"));
        add(txtNombre);
        add(new JLabel("Precio:"));
        add(txtPrecio);
        add(new JLabel("Categoría:"));
        add(comboCategoria);
        add(new JLabel(""));
        add(btnEnviar);

        configurarKafka();

        btnEnviar.addActionListener(e -> enviarProducto());
    }

    private void configurarKafka() {
        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        producer = new KafkaProducer<>(props);
    }

    private void enviarProducto() {
        String nombre = txtNombre.getText();
        String precio = txtPrecio.getText();
        String categoria = (String) comboCategoria.getSelectedItem();

        String mensaje = nombre + " - $" + precio;

        producer.send(new ProducerRecord<>(categoria, mensaje));

        JOptionPane.showMessageDialog(this,
                "Producto enviado al topic [" + categoria + "]: " + mensaje);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ProveedorRopaUI().setVisible(true));
    }
}
