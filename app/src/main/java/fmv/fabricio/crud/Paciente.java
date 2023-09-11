package fmv.fabricio.crud;

public class Paciente {
        private String nombre;
        private String apellido;
        private String descripcion;

        public Paciente(String nombre, String apellido, String descripcion) {
            this.nombre = nombre;
            this.apellido = apellido;
            this.descripcion = descripcion;
        }

        // Getters y setters para los campos nombre, apellido y descripcion

        public String getNombre() {
            return nombre;
        }

        public void setNombre(String nombre) {
            this.nombre = nombre;
        }

        public String getApellido() {
            return apellido;
        }

        public void setApellido(String apellido) {
            this.apellido = apellido;
        }

        public String getDescripcion() {
            return descripcion;
        }

        public void setDescripcion(String descripcion) {
            this.descripcion = descripcion;
        }

        @Override
        public String toString() {
            return "Nombre: " + nombre + "\n"+"Apellido: " + apellido + "\n"+"Descripcion: " + descripcion;
        }


}
