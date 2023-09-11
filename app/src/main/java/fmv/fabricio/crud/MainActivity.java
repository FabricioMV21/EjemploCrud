package fmv.fabricio.crud;

import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    EditText edtNombre, edtApellido, edtDescripcion;
    Button btnAgregar, btnActualizar, btnListar, btnEliminar;
    ListView list2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //Nombre de aplicacion barra principal
        getSupportActionBar().setTitle("Registro Pacientes");

        edtNombre      = findViewById(R.id.edtNombre);
        edtApellido    = findViewById(R.id.edtApellido);
        edtDescripcion = findViewById(R.id.edtDescripcion);
        btnAgregar     = findViewById(R.id.btnAgregar);
        btnActualizar  = findViewById(R.id.btnActualizar);
        btnEliminar    = findViewById(R.id.btnEliminar);
        list2          = findViewById(R.id.list2);

        CargarPacientes();

        btnEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nombre = edtNombre.getText().toString();
                eliminarPaciente(nombre);
                LimpiarCampos();
                CargarPacientes();
            }
        });

        btnAgregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                agregarUsuario();
                LimpiarCampos();
                CargarPacientes();
            }
        });

        btnActualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Nombre = edtNombre.getText().toString();
                actualizarUsuario(Nombre,edtApellido.getText().toString(),edtDescripcion.getText().toString());
                LimpiarCampos();
                CargarPacientes();
            }
        });

        list2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    // Tu código para obtener y mostrar los detalles del paciente seleccionado
                    // Obtén el elemento seleccionado en la posición 'position'
                    Paciente pacienteSeleccionado = listarPacientes().get(position);

                    // Extrae los detalles del paciente seleccionado
                    String nombre = pacienteSeleccionado.getNombre();
                    String apellido = pacienteSeleccionado.getApellido();
                    String descripcion = pacienteSeleccionado.getDescripcion();

                    // Establece los detalles en los campos de texto
                    edtNombre.setText(nombre);
                    edtApellido.setText(apellido);
                    edtDescripcion.setText(descripcion);
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        });

    }

    public Connection conexionBD(){
        Connection conexion = null;
        try {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance();
            conexion = DriverManager.getConnection("jdbc:jtds:sqlserver://192.168.1.22;databaseName=Salud;user=Fabricio;password=1234567890;");
        }catch (Exception e){
            Toast.makeText(this, "ERROR", Toast.LENGTH_SHORT).show();
        }
        return conexion;
    }

    public void agregarUsuario(){
        if(edtNombre.getText().toString().isEmpty()){
            Toast.makeText(this, "EL CAMPO NOMBRE ES OBLIGATORIO", Toast.LENGTH_SHORT).show();
        }else{
            try {
                PreparedStatement pst = conexionBD().prepareStatement("insert into Paciente values(?,?,?)");
                pst.setString(1,edtNombre.getText().toString());
                pst.setString(2,edtApellido.getText().toString());
                pst.setString(3,edtDescripcion.getText().toString());
                pst.executeUpdate();

                Toast.makeText(this, "Registro AGREGADO CORRECTAMENTE", Toast.LENGTH_SHORT).show();
                LimpiarCampos();
            }catch (SQLException e){
                Toast.makeText(this, "ERROR", Toast.LENGTH_SHORT).show();
            }
        }

    }

    public void actualizarUsuario(String nombre, String nuevoApellido, String nuevaDescripcion) {
        try {
            // Preparar una sentencia SQL para actualizar un registro en la tabla "Paciente".
            String sql = "UPDATE Paciente SET Apellido=?, Descripcion=? WHERE Nombre=?";
            PreparedStatement pst = conexionBD().prepareStatement(sql);

            // Establecer los nuevos valores de los campos en la sentencia SQL.
            pst.setString(1, nuevoApellido);
            pst.setString(2, nuevaDescripcion);
            pst.setString(3, nombre); // Establecer el valor del campo Nombre en la cláusula WHERE.

            // Ejecutar la sentencia SQL de actualización.
            int filasAfectadas = pst.executeUpdate();
            if(nombre.isEmpty()){
                Toast.makeText(this, "EL CAMPO NOMBRE ES OBLIGATORIO", Toast.LENGTH_LONG).show();
            }else{
                if (filasAfectadas > 0) {
                    Toast.makeText(this, "Registro ACTUALIZADO CORRECTAMENTE", Toast.LENGTH_SHORT).show();
                    LimpiarCampos();
                } else {
                    Toast.makeText(this, "No se encontró el registro para actualizar", Toast.LENGTH_SHORT).show();
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            Toast.makeText(this, "ERROR al actualizar el registro", Toast.LENGTH_SHORT).show();
        }
    }

    public  List<Paciente> listarPacientes() {
        List<Paciente> listaPacientes = new ArrayList<>();

        try {
            // Preparar una sentencia SQL para obtener todos los registros de la tabla "Paciente".
            String sql = "SELECT * FROM Paciente";
            PreparedStatement pst = conexionBD().prepareStatement(sql);

            //Ejecutar la consulta SQL y obtén el conjunto de resultados.
            ResultSet rs = pst.executeQuery();

            // Iterar a través de los resultados y crear objetos Paciente para cada registro.
            while (rs.next()) {
                String nombre = rs.getString("Nombre");
                String apellido = rs.getString("Apellido");
                String descripcion = rs.getString("Descripcion");

                // Crear un objeto Paciente y agregar los datos a la lista.
                Paciente paciente = new Paciente(nombre, apellido, descripcion);
                listaPacientes.add(paciente);
            }
            // Cerrar la conexión y la consulta.
            rs.close();
            pst.close();
        } catch (SQLException e) {
            e.printStackTrace();
            // Manejo de errores si ocurre una excepción SQL.
        }
        return listaPacientes;
    }

    public void eliminarPaciente(String nombre) {
        if(edtNombre.getText().toString().isEmpty()){
            Toast.makeText(this, "EL CAMPO NOMBRE ES OBLIGATORIO", Toast.LENGTH_SHORT).show();
        }else{
            try {
                Connection connection = conexionBD(); // Establecer la conexión a la base de datos

                // Preparar una sentencia SQL para eliminar un registro en la tabla "Paciente" por nombre
                String sql = "DELETE FROM Paciente WHERE Nombre=?";
                PreparedStatement pst = connection.prepareStatement(sql);

                // Establecer el nombre como parámetro en la sentencia SQL
                pst.setString(1, nombre);

                // Ejecutar la sentencia SQL de eliminación
                int filasAfectadas = pst.executeUpdate();

                if (filasAfectadas > 0) {
                    // Se eliminó correctamente
                    Toast.makeText(this, "El registro se ah eliminado correctamente", Toast.LENGTH_SHORT).show();
                    LimpiarCampos();
                } else {
                    // No se encontró el registro para eliminar
                    Toast.makeText(this, "ERROR NO SE ENCUENTRA EL REGISTRO", Toast.LENGTH_SHORT).show();
                }

                // Cerrar la conexión
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
                // Manejar cualquier error de SQL aquí

            }
        }

    }

    public void CargarPacientes(){
        List<Paciente> pacientes = listarPacientes();

        // Crear un adaptador personalizado de tipo ARRAY
        ArrayAdapter<Paciente> adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, pacientes);

        // Asignar el adaptador al ListView
        list2.setAdapter(adapter);

    }

    public void LimpiarCampos(){
        edtNombre.setText("");
        edtApellido.setText("");
        edtDescripcion.setText("");
    }
}

