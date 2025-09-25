IF NOT EXISTS (SELECT name FROM sys.databases WHERE name = N'tecnonet')
CREATE DATABASE tecnonet;
GO
USE tecnonet;
GO

DROP TABLE IF EXISTS respuestas_solicitudes;
DROP TABLE IF EXISTS solicitudes;
DROP TABLE IF EXISTS facturas;
DROP TABLE IF EXISTS contratos;
DROP TABLE IF EXISTS planes;
DROP TABLE IF EXISTS usuarios;
DROP TABLE IF EXISTS roles;
DROP TABLE IF EXISTS estados_contrato;
DROP TABLE IF EXISTS estados_pago;

CREATE TABLE roles (
    id_rol INT IDENTITY(1,1) PRIMARY KEY,
    nombre_rol VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE estados_contrato (
    id_estado_contrato INT IDENTITY(1,1) PRIMARY KEY,
    nombre_estado VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE estados_pago (
    id_estado_pago INT IDENTITY(1,1) PRIMARY KEY,
    nombre_estado VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE usuarios (
    id_usuario INT IDENTITY(1,1) PRIMARY KEY,
    id_rol INT NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    fecha_registro DATETIME DEFAULT GETDATE(),
    activo BIT DEFAULT 1,
    FOREIGN KEY (id_rol) REFERENCES roles(id_rol)
);

CREATE TABLE planes (
    id_plan INT IDENTITY(1,1) PRIMARY KEY,
    nombre_plan VARCHAR(100) NOT NULL,
    velocidad_descarga_mbps INT NOT NULL,
    velocidad_carga_mbps INT NOT NULL,
    wifi_incluido BIT DEFAULT 1,
    mes_gratis_promocion INT DEFAULT 0,
    puertos_ethernet INT DEFAULT 1,
    precio_mensual DECIMAL(10, 2) NOT NULL,
    descripcion NVARCHAR(MAX),
    fecha_creacion DATETIME DEFAULT GETDATE(),
    activo BIT DEFAULT 1
);

CREATE TABLE contratos (
    id_contrato INT IDENTITY(1,1) PRIMARY KEY,
    id_usuario INT NOT NULL,
    id_plan INT NOT NULL,
    id_estado_contrato INT NOT NULL,
    fecha_contratacion DATE NOT NULL,
    fecha_inicio_servicio DATE NOT NULL,
    fecha_fin_contrato DATE NOT NULL,
    direccion_instalacion VARCHAR(255) NOT NULL,
    numero_telefono_contacto VARCHAR(20),
    metodo_pago VARCHAR(50),
    costo_instalacion DECIMAL(10, 2) DEFAULT 0.00,
    observaciones NVARCHAR(MAX),
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario),
    FOREIGN KEY (id_plan) REFERENCES planes(id_plan),
    FOREIGN KEY (id_estado_contrato) REFERENCES estados_contrato(id_estado_contrato)
);

CREATE TABLE facturas (
    id_factura INT IDENTITY(1,1) PRIMARY KEY,
    id_contrato INT NOT NULL,
    id_estado_pago INT NOT NULL,
    monto_total DECIMAL(10, 2) NOT NULL,
    fecha_emision DATE NOT NULL,
    fecha_vencimiento DATE NOT NULL,
    metodo_pago VARCHAR(50),
    fecha_pago DATE,
    descripcion VARCHAR(255),
    FOREIGN KEY (id_contrato) REFERENCES contratos(id_contrato) ON DELETE CASCADE,
    FOREIGN KEY (id_estado_pago) REFERENCES estados_pago(id_estado_pago)
);

CREATE TABLE solicitudes (
    id_solicitud INT IDENTITY(1,1) PRIMARY KEY,
    id_usuario INT NOT NULL,
    asunto VARCHAR(255) NOT NULL,
    mensaje NVARCHAR(MAX) NOT NULL,
    numero_remitente VARCHAR(20),
    fecha_envio DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario)
);

CREATE TABLE respuestas_solicitudes (
    id_respuesta INT IDENTITY(1,1) PRIMARY KEY,
    id_solicitud INT NOT NULL,
    id_operario INT NOT NULL, 
    respuesta NVARCHAR(MAX) NOT NULL,
    fecha_respuesta DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (id_solicitud) REFERENCES solicitudes(id_solicitud),
    FOREIGN KEY (id_operario) REFERENCES usuarios(id_usuario)
);
GO

IF OBJECT_ID('dbo.GenerarFacturasContrato', 'P') IS NOT NULL
    DROP PROCEDURE dbo.GenerarFacturasContrato;
GO

CREATE PROCEDURE GenerarFacturasContrato(
    @contrato_id INT
)
AS
BEGIN
    DECLARE @v_fecha_inicio DATE;
    DECLARE @v_fecha_fin DATE;
    DECLARE @v_precio_plan DECIMAL(10, 2);
    DECLARE @v_fecha_iteracion DATE;
    DECLARE @v_id_estado_pendiente INT;

    SELECT
        @v_fecha_inicio = c.fecha_inicio_servicio,
        @v_fecha_fin = c.fecha_fin_contrato,
        @v_precio_plan = p.precio_mensual
    FROM contratos c
    JOIN planes p ON c.id_plan = p.id_plan
    WHERE c.id_contrato = @contrato_id;

    SELECT TOP 1 @v_id_estado_pendiente = id_estado_pago
    FROM estados_pago
    WHERE nombre_estado = 'Pendiente';

    SET @v_fecha_iteracion = @v_fecha_inicio;

    WHILE @v_fecha_iteracion < @v_fecha_fin
    BEGIN
        INSERT INTO facturas (
            id_contrato,
            id_estado_pago,
            monto_total,
            fecha_emision,
            fecha_vencimiento,
            descripcion
        ) VALUES (
            @contrato_id,
            @v_id_estado_pendiente,
            @v_precio_plan,
            @v_fecha_iteracion,
            EOMONTH(@v_fecha_iteracion), 
            'Factura correspondiente al mes de ' + DATENAME(MONTH, @v_fecha_iteracion) + ' ' + DATENAME(YEAR, @v_fecha_iteracion) 
        );

        SET @v_fecha_iteracion = DATEADD(MONTH, 1, @v_fecha_iteracion);
    END;

END
GO

EXECUTE GenerarFacturasContrato 1;
GO



INSERT INTO roles (nombre_rol) VALUES
('Administrador'),
('Operario'),
('Cliente');

INSERT INTO estados_pago (nombre_estado) VALUES
('Pendiente'),
('Pagada'),
('Vencida');

INSERT INTO estados_contrato (nombre_estado) VALUES
('Activo'),
('Pendiente de Activación');

INSERT INTO planes (nombre_plan, velocidad_descarga_mbps, velocidad_carga_mbps, precio_mensual, descripcion) VALUES
('Plan Familiar', 300, 150, 45.00, 'Buen plan para la casa');

INSERT INTO usuarios (id_rol, nombre, apellido, email, password_hash) VALUES
(3, 'Ana', 'Gómez', 'ana.gomez@email.com', 'pass_ana'),
(2, 'Carlos', 'Lopez', 'carlos.lopez@tecnonet.com', 'pass_carlos');

INSERT INTO contratos (id_usuario, id_plan, id_estado_contrato, fecha_contratacion, fecha_inicio_servicio, fecha_fin_contrato, direccion_instalacion, numero_telefono_contacto, metodo_pago,costo_instalacion,observaciones) VALUES
(1, 1, 1, '2024-01-10', '2024-02-01', '2025-01-31', 'Avenida Principal 456', 986293698, 'tarjeta de credito', 50.00, 'Buen servicio');

INSERT INTO solicitudes (id_usuario, asunto, mensaje, numero_remitente) VALUES
(1, 'Problema de conexión', 'Mi internet no funciona.', 544778912);

INSERT INTO respuestas_solicitudes (id_solicitud, id_operario, respuesta) VALUES
(1, 2, 'Su solicitud ha sido recibida.');



