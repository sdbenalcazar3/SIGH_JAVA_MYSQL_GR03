/*==============================================================*/
/* Nombre del SGBD:  MySQL 5.7+ / MariaDB 10.x                  */
/* Proyecto:         SIGH_GR3 - Sistema de Gestión Hospitalaria */
/* Modelo físico:    SIGH_GR3_Fisico_MySQL.pdm                  */
/* Script:           DDL - creación de la base de datos         */
/* Fecha:            03/11/2026                                 */
/* Tablas: 88   Claves foráneas: 157                            */
/*==============================================================*/

CREATE DATABASE IF NOT EXISTS SIGH_GR3
   DEFAULT CHARACTER SET utf8mb4
   DEFAULT COLLATE utf8mb4_unicode_ci;

USE SIGH_GR3;

SET FOREIGN_KEY_CHECKS = 0;


/*==============================================================*/
/* Eliminación de tablas existentes                             */
/*==============================================================*/

DROP TABLE IF EXISTS FIREV_REVISI;
DROP TABLE IF EXISTS FICMP_COMPEN;
DROP TABLE IF EXISTS FIDES_DESCUE;
DROP TABLE IF EXISTS FICOM_COMPRO;
DROP TABLE IF EXISTS FIPAG_PAGO;
DROP TABLE IF EXISTS FIRES_RESPON;
DROP TABLE IF EXISTS FITAR_TARIFA;
DROP TABLE IF EXISTS FICAR_CARGOF;
DROP TABLE IF EXISTS FICUE_CUENTA;
DROP TABLE IF EXISTS GEREP_RESPON;
DROP TABLE IF EXISTS GEACT_ACTUAC;
DROP TABLE IF EXISTS GEPCA_PREPAR;
DROP TABLE IF EXISTS GEDOL_DETALL;
DROP TABLE IF EXISTS GEPAR_PARAME;
DROP TABLE IF EXISTS GEEXP_EXPORT;
DROP TABLE IF EXISTS GECRE_CONSUL;
DROP TABLE IF EXISTS GEDEF_DEFINI;
DROP TABLE IF EXISTS GEVIQ_VERSIO;
DROP TABLE IF EXISTS GEIQU_INFORM;
DROP TABLE IF EXISTS GEPAQ_PARTIC;
DROP TABLE IF EXISTS GERQU_RESERV;
DROP TABLE IF EXISTS GEPQU_PROGRA;
DROP TABLE IF EXISTS GESOL_SOLICI;
DROP TABLE IF EXISTS GESAL_SALAQU;
DROP TABLE IF EXISTS GETPR_TIPOPR;
DROP TABLE IF EXISTS GEADJ_ADJUNT;
DROP TABLE IF EXISTS GEVII_VERSIO;
DROP TABLE IF EXISTS GEINF_INFORM;
DROP TABLE IF EXISTS GEPIM_PROGRA;
DROP TABLE IF EXISTS GEOIM_ORDENI;
DROP TABLE IF EXISTS GEREC_RECURS;
DROP TABLE IF EXISTS GETIP_TIPOES;
DROP TABLE IF EXISTS GEAAD_ALTAAD;
DROP TABLE IF EXISTS GEALT_ALTAME;
DROP TABLE IF EXISTS GERCA_RESERV;
DROP TABLE IF EXISTS GEMCA_MOVIMI;
DROP TABLE IF EXISTS GEEST_ESTANC;
DROP TABLE IF EXISTS GEOIN_ORDENI;
DROP TABLE IF EXISTS GECAM_CAMA;
DROP TABLE IF EXISTS GEUNI_UNIDAD;
DROP TABLE IF EXISTS GEDEV_DEVOLU;
DROP TABLE IF EXISTS GEDDI_DETALL;
DROP TABLE IF EXISTS GEDSP_DISPEN;
DROP TABLE IF EXISTS GEMOV_MOVIMI;
DROP TABLE IF EXISTS GELOT_LOTEME;
DROP TABLE IF EXISTS GEPRO_PROVEE;
DROP TABLE IF EXISTS GEMED_MEDICA;
DROP TABLE IF EXISTS GECOM_COMUNI;
DROP TABLE IF EXISTS GEVER_VERSIO;
DROP TABLE IF EXISTS GERES_RESULT;
DROP TABLE IF EXISTS GEMUE_MUESTR;
DROP TABLE IF EXISTS GEORD_ORDENL;
DROP TABLE IF EXISTS GEEXA_EXAMEN;
DROP TABLE IF EXISTS GETRI_TRIAJE;
DROP TABLE IF EXISTS GECUI_CUIDAD;
DROP TABLE IF EXISTS GEDET_DETALL;
DROP TABLE IF EXISTS GEPRE_PRESCR;
DROP TABLE IF EXISTS GEDIA_DIAGNO;
DROP TABLE IF EXISTS GEALE_ALERGI;
DROP TABLE IF EXISTS GEANT_ANTECE;
DROP TABLE IF EXISTS GESIG_SIGNOV;
DROP TABLE IF EXISTS GEADE_ADENDA;
DROP TABLE IF EXISTS GENOT_NOTACL;
DROP TABLE IF EXISTS GEEPI_EPISOD;
DROP TABLE IF EXISTS GEHIS_HISTOR;
DROP TABLE IF EXISTS GECIT_CITA;
DROP TABLE IF EXISTS GEDIS_DISPON;
DROP TABLE IF EXISTS GECON_CONSUL;
DROP TABLE IF EXISTS GECID_CONCIL;
DROP TABLE IF EXISTS GEPAC_PACIEN;
DROP TABLE IF EXISTS SESIS_SISTEM;
DROP TABLE IF EXISTS SEEST_ESTADO;
DROP TABLE IF EXISTS SEAUT_AUTORI;
DROP TABLE IF EXISTS SEEVE_EVENTO;
DROP TABLE IF EXISTS SECRE_CREDEN;
DROP TABLE IF EXISTS SEOXP_OPCPER;
DROP TABLE IF EXISTS SEUXP_USUPER;
DROP TABLE IF EXISTS SEOPC_OPCION;
DROP TABLE IF EXISTS SEPER_PERFIL;
DROP TABLE IF EXISTS SESES_SESION;
DROP TABLE IF EXISTS SEUSU_USUARI;
DROP TABLE IF EXISTS PEHAB_HABILI;
DROP TABLE IF EXISTS PECAR_CARGO;
DROP TABLE IF EXISTS PEDEP_DEPART;
DROP TABLE IF EXISTS PEEST_ESTADO;
DROP TABLE IF EXISTS PEGEN_GENERO;
DROP TABLE IF EXISTS PEESP_ESPECI;
DROP TABLE IF EXISTS PEPER_PERSON;

SET FOREIGN_KEY_CHECKS = 1;

/*==============================================================*/
/* Tabla: PEPER_PERSON (PERSONA)                                */
/*==============================================================*/

CREATE TABLE PEPER_PERSON
(
   PEPER_CODIGO  CHAR(6)       NOT NULL COMMENT 'CodigoPersona',
   PEPER_CODEXT  CHAR(10)      NOT NULL COMMENT 'CodigoExternoPersona',
   PEPER_NOMBRE  VARCHAR(255)  NOT NULL COMMENT 'NombresPersona',
   PEPER_FUNCIO  VARCHAR(255)  NOT NULL COMMENT 'FuncionPersona',
   PEPER_HABILI  VARCHAR(255)  NULL     COMMENT 'HabilitacionPersona',
   PEPER_ACTIVO  BOOL          NOT NULL COMMENT 'ActivoPersona',
   PEPER_APELLI  VARCHAR(150)  NOT NULL COMMENT 'ApellidosPersona',
   PEPER_TIPDOC  VARCHAR(30)   NULL     COMMENT 'TipoDocumentoPersona',
   PEPER_NUMDOC  VARCHAR(30)   NULL     COMMENT 'NumeroDocumentoPersona',
   PEPER_FECNAC  DATE          NULL     COMMENT 'FechaNacimientoPersona',
   PEPER_TELEFO  VARCHAR(30)   NULL     COMMENT 'TelefonoPersona',
   PEPER_CORREO  VARCHAR(150)  NULL     COMMENT 'CorreoPersona',
   PEGEN_CODIGO  CHAR(1)       NULL     COMMENT 'CodigoGenero: Clave foránea hacia GENERO (relación generoPersonal).',
   PEEST_CODIGO  CHAR(1)       NULL     COMMENT 'CodigoEstadoCivil: Clave foránea hacia ESTADO_CIVIL (relación estadoCivilPersonal).',
   PEDEP_CODIGO  CHAR(3)       NOT NULL COMMENT 'CodigoDepartamento: Clave foránea hacia DEPARTAMENTO (relación departamentoPersonal).',
   PECAR_CODIGO  CHAR(3)       NOT NULL COMMENT 'CodigoCargo: Clave foránea hacia CARGO (relación cargoPersonal).',
   CONSTRAINT PK_PEPER PRIMARY KEY (PEPER_CODIGO)
)
ENGINE = InnoDB
COMMENT = 'PERSONA. Entidad utilizada para gestionar las personas (empleados) que harán uso del sistema.';

CREATE INDEX IX_PEPER_PEGEN ON PEPER_PERSON (PEGEN_CODIGO);

CREATE INDEX IX_PEPER_PEEST ON PEPER_PERSON (PEEST_CODIGO);

CREATE INDEX IX_PEPER_PEDEP ON PEPER_PERSON (PEDEP_CODIGO);

CREATE INDEX IX_PEPER_PECAR ON PEPER_PERSON (PECAR_CODIGO);

/*==============================================================*/
/* Tabla: PEESP_ESPECI (ESPECIALIDAD)                           */
/*==============================================================*/

CREATE TABLE PEESP_ESPECI
(
   PEESP_CODIGO  CHAR(3)       NOT NULL COMMENT 'CodigoEspecialidad',
   PEESP_CODEXT  CHAR(10)      NOT NULL COMMENT 'CodigoExternoEspecialidad',
   PEESP_NOMBRE  VARCHAR(255)  NOT NULL COMMENT 'NombreEspecialidad',
   PEESP_ACTIVO  BOOL          NOT NULL COMMENT 'ActivoEspecialidad',
   CONSTRAINT PK_PEESP PRIMARY KEY (PEESP_CODIGO)
)
ENGINE = InnoDB
COMMENT = 'ESPECIALIDAD. Entidad utilizada para gestionar las especialidades médicas del hospital.';

/*==============================================================*/
/* Tabla: PEGEN_GENERO (GENERO)                                 */
/*==============================================================*/

CREATE TABLE PEGEN_GENERO
(
   PEGEN_CODIGO  CHAR(1)       NOT NULL COMMENT 'CodigoGenero',
   PEGEN_NOMBRE  VARCHAR(100)  NOT NULL COMMENT 'NombreGenero',
   PEGEN_ACTIVO  BOOL          NOT NULL COMMENT 'ActivoGenero',
   CONSTRAINT PK_PEGEN PRIMARY KEY (PEGEN_CODIGO)
)
ENGINE = InnoDB
COMMENT = 'GENERO. Entidad utilizada para registrar los géneros de las personas y pacientes.';

/*==============================================================*/
/* Tabla: PEEST_ESTADO (ESTADO_CIVIL)                           */
/*==============================================================*/

CREATE TABLE PEEST_ESTADO
(
   PEEST_CODIGO  CHAR(1)       NOT NULL COMMENT 'CodigoEstadoCivil',
   PEEST_NOMBRE  VARCHAR(100)  NOT NULL COMMENT 'NombreEstadoCivil',
   PEEST_ACTIVO  BOOL          NOT NULL COMMENT 'ActivoEstadoCivil',
   CONSTRAINT PK_PEEST PRIMARY KEY (PEEST_CODIGO)
)
ENGINE = InnoDB
COMMENT = 'ESTADO_CIVIL. Entidad utilizada para registrar los estados civiles de las personas y pacientes.';

/*==============================================================*/
/* Tabla: PEDEP_DEPART (DEPARTAMENTO)                           */
/*==============================================================*/

CREATE TABLE PEDEP_DEPART
(
   PEDEP_CODIGO  CHAR(3)       NOT NULL COMMENT 'CodigoDepartamento',
   PEDEP_NOMBRE  VARCHAR(100)  NOT NULL COMMENT 'NombreDepartamento',
   PEDEP_ACTIVO  BOOL          NOT NULL COMMENT 'ActivoDepartamento',
   CONSTRAINT PK_PEDEP PRIMARY KEY (PEDEP_CODIGO)
)
ENGINE = InnoDB
COMMENT = 'DEPARTAMENTO. Entidad utilizada para gestionar los departamentos del hospital.';

/*==============================================================*/
/* Tabla: PECAR_CARGO (CARGO)                                   */
/*==============================================================*/

CREATE TABLE PECAR_CARGO
(
   PECAR_CODIGO  CHAR(3)       NOT NULL COMMENT 'CodigoCargo',
   PECAR_NOMBRE  VARCHAR(100)  NOT NULL COMMENT 'NombreCargo',
   PECAR_ACTIVO  BOOL          NOT NULL COMMENT 'ActivoCargo',
   CONSTRAINT PK_PECAR PRIMARY KEY (PECAR_CODIGO)
)
ENGINE = InnoDB
COMMENT = 'CARGO. Entidad utilizada para gestionar los cargos que ocupan los empleados.';

/*==============================================================*/
/* Tabla: PEHAB_HABILI (HABILITACION_ESPECIALIDAD)              */
/*==============================================================*/

CREATE TABLE PEHAB_HABILI
(
   PEHAB_CODIGO  CHAR(6)      NOT NULL COMMENT 'CodigoHabilitacionEspecialidad',
   PEHAB_REGPRO  VARCHAR(60)  NOT NULL COMMENT 'RegistroProfesionalHabilitacionEspecialidad',
   PEHAB_DESDE   DATE         NOT NULL COMMENT 'DesdeHabilitacionEspecialidad',
   PEHAB_HASTA   DATE         NULL     COMMENT 'HastaHabilitacionEspecialidad',
   PEHAB_VIGENT  BOOL         NOT NULL COMMENT 'VigenteHabilitacionEspecialidad',
   PEPER_CODIGO  CHAR(6)      NOT NULL COMMENT 'CodigoPersona: Clave foránea hacia PERSONA (relación habilitaciones).',
   PEESP_CODIGO  CHAR(3)      NOT NULL COMMENT 'CodigoEspecialidad: Clave foránea hacia ESPECIALIDAD (relación profesionalesHabilitados).',
   CONSTRAINT PK_PEHAB PRIMARY KEY (PEHAB_CODIGO)
)
ENGINE = InnoDB
COMMENT = 'HABILITACION_ESPECIALIDAD. Entidad utilizada para registrar las especialidades en las que está habilitado cada profesional.';

CREATE INDEX IX_PEHAB_PEPER ON PEHAB_HABILI (PEPER_CODIGO);

CREATE INDEX IX_PEHAB_PEESP ON PEHAB_HABILI (PEESP_CODIGO);

/*==============================================================*/
/* Tabla: SEUSU_USUARI (USUARIO)                                */
/*==============================================================*/

CREATE TABLE SEUSU_USUARI
(
   SEUSU_CODIGO  CHAR(6)       NOT NULL COMMENT 'CodigoUsuario',
   SEUSU_USUARI  VARCHAR(255)  NOT NULL COMMENT 'UsuarioUsuario',
   SEUSU_HASHCL  VARCHAR(255)  NOT NULL COMMENT 'HashClaveUsuario',
   SEUSU_INTFAL  INT           NOT NULL COMMENT 'IntentosFallidosUsuario',
   SEUSU_BLOHAS  DATETIME      NULL     COMMENT 'BloqueoHastaUsuario',
   SEUSU_FECCRE  DATETIME      NOT NULL COMMENT 'FechaCreacionUsuario',
   SEUSU_FECMOD  DATETIME      NULL     COMMENT 'FechaModificacionUsuario',
   SEUSU_PIEFIR  VARCHAR(200)  NULL     COMMENT 'PieFirmaUsuario',
   PEPER_CODIGO  CHAR(6)       NOT NULL COMMENT 'CodigoPersona: Clave foránea hacia PERSONA (relación cuenta).',
   SEEST_CODIGO  CHAR(1)       NOT NULL COMMENT 'CodigoEstado: Clave foránea hacia ESTADO (relación estadoCuenta).',
   CONSTRAINT PK_SEUSU PRIMARY KEY (SEUSU_CODIGO),
   CONSTRAINT AK_SEUSU_1 UNIQUE (SEUSU_USUARI),
   CONSTRAINT AK_SEUSU_2 UNIQUE (PEPER_CODIGO)
)
ENGINE = InnoDB
COMMENT = 'USUARIO. Entidad utilizada para gestionar las cuentas de acceso de las personas al sistema.';

CREATE INDEX IX_SEUSU_SEEST ON SEUSU_USUARI (SEEST_CODIGO);

/*==============================================================*/
/* Tabla: SESES_SESION (SESION)                                 */
/*==============================================================*/

CREATE TABLE SESES_SESION
(
   SESES_CODIGO  CHAR(12)      NOT NULL COMMENT 'CodigoSesion',
   SESES_INICIO  DATETIME      NOT NULL COMMENT 'InicioSesion',
   SESES_ULTACT  DATETIME      NOT NULL COMMENT 'UltimaActividadSesion',
   SESES_EXPIRA  DATETIME      NOT NULL COMMENT 'ExpiraSesion',
   SESES_ESTADO  VARCHAR(255)  NOT NULL COMMENT 'EstadoSesion',
   SESES_LIMITA  BOOL          NOT NULL COMMENT 'LimitadaSesion',
   SEUSU_CODIGO  CHAR(6)       NOT NULL COMMENT 'CodigoUsuario: Clave foránea hacia USUARIO (relación sesiones).',
   CONSTRAINT PK_SESES PRIMARY KEY (SESES_CODIGO)
)
ENGINE = InnoDB
COMMENT = 'SESION. Entidad utilizada para registrar las sesiones iniciadas por los usuarios.';

CREATE INDEX IX_SESES_SEUSU ON SESES_SESION (SEUSU_CODIGO);

/*==============================================================*/
/* Tabla: SEPER_PERFIL (PERFIL)                                 */
/*==============================================================*/

CREATE TABLE SEPER_PERFIL
(
   SEPER_CODIGO  CHAR(3)       NOT NULL COMMENT 'CodigoPerfil',
   SEPER_NOMBRE  VARCHAR(255)  NOT NULL COMMENT 'NombrePerfil',
   SEPER_ACTIVO  BOOL          NOT NULL COMMENT 'ActivoPerfil',
   SEPER_DESCRI  TEXT          NULL     COMMENT 'DescripcionPerfil',
   SEPER_OBSERV  TEXT          NULL     COMMENT 'ObservacionesPerfil',
   SEEST_CODIGO  CHAR(1)       NOT NULL COMMENT 'CodigoEstado: Clave foránea hacia ESTADO (relación estadoPerfil).',
   CONSTRAINT PK_SEPER PRIMARY KEY (SEPER_CODIGO),
   CONSTRAINT AK_SEPER_1 UNIQUE (SEPER_NOMBRE)
)
ENGINE = InnoDB
COMMENT = 'PERFIL. Entidad utilizada para gestionar los perfiles (roles) del sistema.';

CREATE INDEX IX_SEPER_SEEST ON SEPER_PERFIL (SEEST_CODIGO);

/*==============================================================*/
/* Tabla: SEOPC_OPCION (OPCION)                                 */
/*==============================================================*/

CREATE TABLE SEOPC_OPCION
(
   SEOPC_CODIGO  CHAR(3)       NOT NULL COMMENT 'CodigoOpcion',
   SEOPC_MODULO  VARCHAR(255)  NOT NULL COMMENT 'ModuloOpcion',
   SEOPC_OPERAC  VARCHAR(255)  NOT NULL COMMENT 'OperacionOpcion',
   SEOPC_ACTIVO  BOOL          NOT NULL COMMENT 'ActivoOpcion',
   SEOPC_NOMBRE  VARCHAR(100)  NOT NULL COMMENT 'NombreOpcion',
   SEOPC_DESCRI  TEXT          NULL     COMMENT 'DescripcionOpcion',
   SESIS_CODIGO  CHAR(2)       NOT NULL COMMENT 'CodigoSistema: Clave foránea hacia SISTEMA (relación operacionesSistema).',
   SEEST_CODIGO  CHAR(1)       NOT NULL COMMENT 'CodigoEstado: Clave foránea hacia ESTADO (relación estadoOpcion).',
   CONSTRAINT PK_SEOPC PRIMARY KEY (SEOPC_CODIGO)
)
ENGINE = InnoDB
COMMENT = 'OPCION. Entidad utilizada para gestionar las opciones del sistema que se asignan a los perfiles.';

CREATE INDEX IX_SEOPC_SESIS ON SEOPC_OPCION (SESIS_CODIGO);

CREATE INDEX IX_SEOPC_SEEST ON SEOPC_OPCION (SEEST_CODIGO);

/*==============================================================*/
/* Tabla: SEUXP_USUPER (USUPER)                                 */
/*==============================================================*/

CREATE TABLE SEUXP_USUPER
(
   SEUXP_CODIGO         CHAR(6)       NOT NULL COMMENT 'CodigoUsuper',
   SEUXP_DESDE          DATETIME      NOT NULL COMMENT 'DesdeUsuper',
   SEUXP_HASTA          DATETIME      NULL     COMMENT 'HastaUsuper',
   SEUXP_AMBITO         VARCHAR(255)  NOT NULL COMMENT 'AmbitoUsuper',
   SEUXP_VIGENT         BOOL          NOT NULL COMMENT 'VigenteUsuper',
   SEUSU_CODIGO         CHAR(6)       NOT NULL COMMENT 'CodigoUsuario: Clave foránea hacia USUARIO (relación rolesAsignados).',
   SEPER_CODIGO         CHAR(3)       NOT NULL COMMENT 'CodigoPerfil: Clave foránea hacia PERFIL (relación asignaciones).',
   SEUSU_CODIGO_ASIGNA  CHAR(6)       NOT NULL COMMENT 'CodigoUsuarioAsigna: Clave foránea hacia USUARIO (relación asignaPerfil).',
   CONSTRAINT PK_SEUXP PRIMARY KEY (SEUXP_CODIGO)
)
ENGINE = InnoDB
COMMENT = 'USUPER. Entidad utilizada para asignar perfiles a los usuarios.';

CREATE INDEX IX_SEUXP_SEUSU ON SEUXP_USUPER (SEUSU_CODIGO);

CREATE INDEX IX_SEUXP_SEPER ON SEUXP_USUPER (SEPER_CODIGO);

CREATE INDEX IX_SEUXP_SEUSU_ASIGNA ON SEUXP_USUPER (SEUSU_CODIGO_ASIGNA);

/*==============================================================*/
/* Tabla: SEOXP_OPCPER (OPC_PER)                                */
/*==============================================================*/

CREATE TABLE SEOXP_OPCPER
(
   SEOXP_CODIGO  CHAR(6)       NOT NULL COMMENT 'CodigoOpcPer',
   SEOXP_AMBITO  VARCHAR(255)  NOT NULL COMMENT 'AmbitoOpcPer',
   SEOXP_VIGENT  BOOL          NOT NULL COMMENT 'VigenteOpcPer',
   SEOXP_DESDE   DATETIME      NOT NULL COMMENT 'DesdeOpcPer',
   SEOXP_HASTA   DATETIME      NULL     COMMENT 'HastaOpcPer',
   SEPER_CODIGO  CHAR(3)       NOT NULL COMMENT 'CodigoPerfil: Clave foránea hacia PERFIL (relación permisos).',
   SEOPC_CODIGO  CHAR(3)       NOT NULL COMMENT 'CodigoOpcion: Clave foránea hacia OPCION (relación concesiones).',
   SEUSU_CODIGO  CHAR(6)       NOT NULL COMMENT 'CodigoUsuario: Clave foránea hacia USUARIO (relación concedeOperacion).',
   CONSTRAINT PK_SEOXP PRIMARY KEY (SEOXP_CODIGO)
)
ENGINE = InnoDB
COMMENT = 'OPC_PER. Entidad utilizada para asignar opciones a los perfiles.';

CREATE INDEX IX_SEOXP_SEPER ON SEOXP_OPCPER (SEPER_CODIGO);

CREATE INDEX IX_SEOXP_SEOPC ON SEOXP_OPCPER (SEOPC_CODIGO);

CREATE INDEX IX_SEOXP_SEUSU ON SEOXP_OPCPER (SEUSU_CODIGO);

/*==============================================================*/
/* Tabla: SECRE_CREDEN (CREDENCIAL_TEMPORAL)                    */
/*==============================================================*/

CREATE TABLE SECRE_CREDEN
(
   SECRE_CODIGO  CHAR(10)      NOT NULL COMMENT 'CodigoCredencialTemporal',
   SECRE_HASH    VARCHAR(255)  NOT NULL COMMENT 'HashCredencialTemporal',
   SECRE_EMITID  DATETIME      NOT NULL COMMENT 'EmitidaCredencialTemporal',
   SECRE_CONSUM  DATETIME      NULL     COMMENT 'ConsumidaCredencialTemporal',
   SECRE_ESTADO  VARCHAR(255)  NOT NULL COMMENT 'EstadoCredencialTemporal',
   SEUSU_CODIGO  CHAR(6)       NOT NULL COMMENT 'CodigoUsuario: Clave foránea hacia USUARIO (relación credenciales).',
   CONSTRAINT PK_SECRE PRIMARY KEY (SECRE_CODIGO)
)
ENGINE = InnoDB
COMMENT = 'CREDENCIAL_TEMPORAL. Entidad utilizada para registrar las credenciales temporales entregadas a los usuarios.';

CREATE INDEX IX_SECRE_SEUSU ON SECRE_CREDEN (SEUSU_CODIGO);

/*==============================================================*/
/* Tabla: SEEVE_EVENTO (EVENTO_AUDITORIA)                       */
/*==============================================================*/

CREATE TABLE SEEVE_EVENTO
(
   SEEVE_CODIGO  CHAR(12)      NOT NULL COMMENT 'CodigoEventoAuditoria',
   SEEVE_INSTAN  DATETIME      NOT NULL COMMENT 'InstanteEventoAuditoria',
   SEEVE_ACCION  VARCHAR(255)  NOT NULL COMMENT 'AccionEventoAuditoria',
   SEEVE_ENTIDA  VARCHAR(255)  NOT NULL COMMENT 'EntidadEventoAuditoria',
   SEEVE_REFERE  VARCHAR(255)  NOT NULL COMMENT 'ReferenciaEventoAuditoria',
   SEEVE_RESULT  VARCHAR(255)  NOT NULL COMMENT 'ResultadoEventoAuditoria',
   SEEVE_ORIGEN  VARCHAR(255)  NULL     COMMENT 'OrigenEventoAuditoria',
   SEEVE_DETCAM  TEXT          NULL     COMMENT 'DetalleCambioEventoAuditoria',
   SEUSU_CODIGO  CHAR(6)       NULL     COMMENT 'CodigoUsuario: Clave foránea hacia USUARIO (relación actorEvento).',
   SEAUT_CODIGO  CHAR(8)       NULL     COMMENT 'CodigoAutorizacionExcepcional: Clave foránea hacia AUTORIZACION_EXCEPCIONAL (relación amparaAcceso).',
   CONSTRAINT PK_SEEVE PRIMARY KEY (SEEVE_CODIGO)
)
ENGINE = InnoDB
COMMENT = 'EVENTO_AUDITORIA. Entidad utilizada para registrar los eventos de auditoría del sistema.';

CREATE INDEX IX_SEEVE_SEUSU ON SEEVE_EVENTO (SEUSU_CODIGO);

CREATE INDEX IX_SEEVE_SEAUT ON SEEVE_EVENTO (SEAUT_CODIGO);

/*==============================================================*/
/* Tabla: SEAUT_AUTORI (AUTORIZACION_EXCEPCIONAL)               */
/*==============================================================*/

CREATE TABLE SEAUT_AUTORI
(
   SEAUT_CODIGO         CHAR(8)       NOT NULL COMMENT 'CodigoAutorizacionExcepcional',
   SEAUT_MOTIVO         TEXT          NOT NULL COMMENT 'MotivoAutorizacionExcepcional',
   SEAUT_DESDE          DATETIME      NOT NULL COMMENT 'DesdeAutorizacionExcepcional',
   SEAUT_HASTA          DATETIME      NULL     COMMENT 'HastaAutorizacionExcepcional',
   SEAUT_AMBITO         VARCHAR(255)  NOT NULL COMMENT 'AmbitoAutorizacionExcepcional',
   SEAUT_EVIDEN         TEXT          NULL     COMMENT 'EvidenciaAutorizacionExcepcional',
   SEAUT_FECAPR         DATETIME      NOT NULL COMMENT 'FechaAprobacionAutorizacionExcepcional',
   SEUSU_CODIGO_AUDITO  CHAR(6)       NOT NULL COMMENT 'CodigoUsuarioAuditor: Clave foránea hacia USUARIO (relación auditorAutorizado).',
   SEUSU_CODIGO_APRUEB  CHAR(6)       NOT NULL COMMENT 'CodigoUsuarioAprueba: Clave foránea hacia USUARIO (relación apruebaAccesoExcepcional).',
   CONSTRAINT PK_SEAUT PRIMARY KEY (SEAUT_CODIGO)
)
ENGINE = InnoDB
COMMENT = 'AUTORIZACION_EXCEPCIONAL. Entidad utilizada para registrar los accesos excepcionales autorizados.';

CREATE INDEX IX_SEAUT_SEUSU_AUDITO ON SEAUT_AUTORI (SEUSU_CODIGO_AUDITO);

CREATE INDEX IX_SEAUT_SEUSU_APRUEB ON SEAUT_AUTORI (SEUSU_CODIGO_APRUEB);

/*==============================================================*/
/* Tabla: SEEST_ESTADO (ESTADO)                                 */
/*==============================================================*/

CREATE TABLE SEEST_ESTADO
(
   SEEST_CODIGO  CHAR(1)       NOT NULL COMMENT 'CodigoEstado',
   SEEST_NOMBRE  VARCHAR(100)  NOT NULL COMMENT 'NombreEstado',
   SEEST_ACTIVO  BOOL          NOT NULL COMMENT 'ActivoEstado',
   CONSTRAINT PK_SEEST PRIMARY KEY (SEEST_CODIGO)
)
ENGINE = InnoDB
COMMENT = 'ESTADO. Entidad utilizada para registrar los estados (activo/inactivo) de usuarios, perfiles y opciones.';

/*==============================================================*/
/* Tabla: SESIS_SISTEM (SISTEMA)                                */
/*==============================================================*/

CREATE TABLE SESIS_SISTEM
(
   SESIS_CODIGO  CHAR(2)       NOT NULL COMMENT 'CodigoSistema',
   SESIS_NOMBRE  VARCHAR(100)  NOT NULL COMMENT 'NombreSistema',
   SESIS_ACTIVO  BOOL          NOT NULL COMMENT 'ActivoSistema',
   CONSTRAINT PK_SESIS PRIMARY KEY (SESIS_CODIGO)
)
ENGINE = InnoDB
COMMENT = 'SISTEMA. Entidad utilizada para registrar los subsistemas (módulos) del sistema.';

/*==============================================================*/
/* Tabla: GEPAC_PACIEN (PACIENTE)                               */
/*==============================================================*/

CREATE TABLE GEPAC_PACIEN
(
   GEPAC_CODIGO  CHAR(10)      NOT NULL COMMENT 'CodigoPaciente',
   GEPAC_TIPDOC  VARCHAR(255)  NULL     COMMENT 'TipoDocumentoPaciente',
   GEPAC_NUMDOC  VARCHAR(255)  NULL     COMMENT 'NumeroDocumentoPaciente',
   GEPAC_NOMBRE  VARCHAR(255)  NULL     COMMENT 'NombresPaciente',
   GEPAC_FECNAC  DATE          NULL     COMMENT 'NacimientoPaciente',
   GEPAC_ESTIDE  VARCHAR(255)  NOT NULL COMMENT 'EstadoIdentidadPaciente',
   GEPAC_CONTAC  VARCHAR(255)  NULL     COMMENT 'ContactoPaciente',
   GEPAC_APELLI  VARCHAR(150)  NULL     COMMENT 'ApellidosPaciente',
   GEPAC_FECREG  DATETIME      NOT NULL COMMENT 'FechaRegistroPaciente',
   GEPAC_TIPIDE  VARCHAR(30)   NOT NULL COMMENT 'TipoIdentidadPaciente',
   GEPAC_ESTADO  VARCHAR(40)   NOT NULL COMMENT 'EstadoPaciente',
   GEPAC_DIRECC  VARCHAR(255)  NULL     COMMENT 'DireccionPaciente',
   GEPAC_CONEME  VARCHAR(200)  NULL     COMMENT 'ContactoEmergenciaPaciente',
   PEGEN_CODIGO  CHAR(1)       NULL     COMMENT 'CodigoGenero: Clave foránea hacia GENERO (relación generoPaciente).',
   PEEST_CODIGO  CHAR(1)       NULL     COMMENT 'CodigoEstadoCivil: Clave foránea hacia ESTADO_CIVIL (relación estadoCivilPaciente).',
   CONSTRAINT PK_GEPAC PRIMARY KEY (GEPAC_CODIGO)
)
ENGINE = InnoDB
COMMENT = 'PACIENTE. Entidad utilizada para gestionar los pacientes que reciben atención en el hospital.';

CREATE INDEX IX_GEPAC_PEGEN ON GEPAC_PACIEN (PEGEN_CODIGO);

CREATE INDEX IX_GEPAC_PEEST ON GEPAC_PACIEN (PEEST_CODIGO);

/*==============================================================*/
/* Tabla: GECID_CONCIL (CONCILIACION_IDENTIDAD)                 */
/*==============================================================*/

CREATE TABLE GECID_CONCIL
(
   GECID_CODIGO         CHAR(8)       NOT NULL COMMENT 'CodigoConciliacionIdentidad',
   GECID_INSTAN         DATETIME      NOT NULL COMMENT 'InstanteConciliacionIdentidad',
   GECID_EVIDEN         TEXT          NOT NULL COMMENT 'EvidenciaConciliacionIdentidad',
   GECID_MOTIVO         TEXT          NOT NULL COMMENT 'MotivoConciliacionIdentidad',
   GECID_ESTADO         VARCHAR(255)  NOT NULL COMMENT 'EstadoConciliacionIdentidad',
   GEPAC_CODIGO_DESTIN  CHAR(10)      NOT NULL COMMENT 'CodigoPacienteDestino: Clave foránea hacia PACIENTE (relación destinoConciliado).',
   GEPAC_CODIGO_ORIGEN  CHAR(10)      NOT NULL COMMENT 'CodigoPacienteOrigen: Clave foránea hacia PACIENTE (relación origenProvisional).',
   SEUSU_CODIGO         CHAR(6)       NOT NULL COMMENT 'CodigoUsuario: Clave foránea hacia USUARIO (relación autorizaConciliacion).',
   CONSTRAINT PK_GECID PRIMARY KEY (GECID_CODIGO)
)
ENGINE = InnoDB
COMMENT = 'CONCILIACION_IDENTIDAD. Entidad utilizada para registrar la unión de un paciente provisional con su identidad definitiva.';

CREATE INDEX IX_GECID_GEPAC_DESTIN ON GECID_CONCIL (GEPAC_CODIGO_DESTIN);

CREATE INDEX IX_GECID_GEPAC_ORIGEN ON GECID_CONCIL (GEPAC_CODIGO_ORIGEN);

CREATE INDEX IX_GECID_SEUSU ON GECID_CONCIL (SEUSU_CODIGO);

/*==============================================================*/
/* Tabla: GECON_CONSUL (CONSULTORIO)                            */
/*==============================================================*/

CREATE TABLE GECON_CONSUL
(
   GECON_CODIGO  CHAR(3)       NOT NULL COMMENT 'CodigoConsultorio',
   GECON_CODEXT  CHAR(10)      NOT NULL COMMENT 'CodigoExternoConsultorio',
   GECON_NOMBRE  VARCHAR(255)  NOT NULL COMMENT 'NombreConsultorio',
   GECON_ACTIVO  BOOL          NOT NULL COMMENT 'ActivoConsultorio',
   CONSTRAINT PK_GECON PRIMARY KEY (GECON_CODIGO)
)
ENGINE = InnoDB
COMMENT = 'CONSULTORIO. Entidad utilizada para gestionar los consultorios del hospital.';

/*==============================================================*/
/* Tabla: GEDIS_DISPON (DISPONIBILIDAD)                         */
/*==============================================================*/

CREATE TABLE GEDIS_DISPON
(
   GEDIS_CODIGO  CHAR(8)   NOT NULL COMMENT 'CodigoDisponibilidad',
   GEDIS_INICIO  DATETIME  NOT NULL COMMENT 'InicioDisponibilidad',
   GEDIS_FIN     DATETIME  NOT NULL COMMENT 'FinDisponibilidad',
   GEDIS_DURMIN  INT       NOT NULL COMMENT 'DuracionMinutosDisponibilidad',
   GEDIS_VIGENT  BOOL      NOT NULL COMMENT 'VigenteDisponibilidad',
   PEESP_CODIGO  CHAR(3)   NOT NULL COMMENT 'CodigoEspecialidad: Clave foránea hacia ESPECIALIDAD (relación horarios).',
   PEPER_CODIGO  CHAR(6)   NOT NULL COMMENT 'CodigoPersona: Clave foránea hacia PERSONA (relación profesional).',
   GECON_CODIGO  CHAR(3)   NOT NULL COMMENT 'CodigoConsultorio: Clave foránea hacia CONSULTORIO (relación disponibilidad).',
   CONSTRAINT PK_GEDIS PRIMARY KEY (GEDIS_CODIGO)
)
ENGINE = InnoDB
COMMENT = 'DISPONIBILIDAD. Entidad utilizada para registrar los horarios disponibles de los profesionales.';

CREATE INDEX IX_GEDIS_PEESP ON GEDIS_DISPON (PEESP_CODIGO);

CREATE INDEX IX_GEDIS_PEPER ON GEDIS_DISPON (PEPER_CODIGO);

CREATE INDEX IX_GEDIS_GECON ON GEDIS_DISPON (GECON_CODIGO);

/*==============================================================*/
/* Tabla: GECIT_CITA (CITA)                                     */
/*==============================================================*/

CREATE TABLE GECIT_CITA
(
   GECIT_CODIGO         CHAR(10)      NOT NULL COMMENT 'CodigoCita',
   GECIT_INICIO         DATETIME      NOT NULL COMMENT 'InicioCita',
   GECIT_FIN            DATETIME      NOT NULL COMMENT 'FinCita',
   GECIT_ESTADO         VARCHAR(255)  NOT NULL COMMENT 'EstadoCita',
   GECIT_MOTIVO         TEXT          NOT NULL COMMENT 'MotivoCita',
   GECIT_LLEGAD         DATETIME      NULL     COMMENT 'LlegadaCita',
   GECIT_MOTCAM         VARCHAR(255)  NULL     COMMENT 'MotivoCambioCita',
   GECIT_CONFIR         DATETIME      NULL     COMMENT 'ConfirmacionCita',
   GEPAC_CODIGO         CHAR(10)      NOT NULL COMMENT 'CodigoPaciente: Clave foránea hacia PACIENTE (relación citas).',
   GEDIS_CODIGO         CHAR(8)       NOT NULL COMMENT 'CodigoDisponibilidad: Clave foránea hacia DISPONIBILIDAD (relación reservas).',
   PEPER_CODIGO         CHAR(6)       NOT NULL COMMENT 'CodigoPersona: Clave foránea hacia PERSONA (relación medicoAgenda).',
   GECON_CODIGO         CHAR(3)       NOT NULL COMMENT 'CodigoConsultorio: Clave foránea hacia CONSULTORIO (relación consultorioCita).',
   PEESP_CODIGO         CHAR(3)       NOT NULL COMMENT 'CodigoEspecialidad: Clave foránea hacia ESPECIALIDAD (relación especialidadCita).',
   GECIT_CODIGO_REPROG  CHAR(10)      NULL     COMMENT 'CodigoCitaReprogramada: Clave foránea hacia CITA (relación reprograma).',
   CONSTRAINT PK_GECIT PRIMARY KEY (GECIT_CODIGO)
)
ENGINE = InnoDB
COMMENT = 'CITA. Entidad utilizada para gestionar las citas médicas de los pacientes.';

CREATE UNIQUE INDEX UX_GECIT_GECIT_REPROG ON GECIT_CITA (GECIT_CODIGO_REPROG);

CREATE INDEX IX_GECIT_GEPAC ON GECIT_CITA (GEPAC_CODIGO);

CREATE INDEX IX_GECIT_GEDIS ON GECIT_CITA (GEDIS_CODIGO);

CREATE INDEX IX_GECIT_PEPER ON GECIT_CITA (PEPER_CODIGO);

CREATE INDEX IX_GECIT_GECON ON GECIT_CITA (GECON_CODIGO);

CREATE INDEX IX_GECIT_PEESP ON GECIT_CITA (PEESP_CODIGO);

/*==============================================================*/
/* Tabla: GEHIS_HISTOR (HISTORIA_CLINICA)                       */
/*==============================================================*/

CREATE TABLE GEHIS_HISTOR
(
   GEHIS_CODIGO  CHAR(10)      NOT NULL COMMENT 'CodigoHistoriaClinica',
   GEHIS_NUMERO  VARCHAR(255)  NOT NULL COMMENT 'NumeroHistoriaClinica',
   GEHIS_CREADA  DATETIME      NOT NULL COMMENT 'CreadaHistoriaClinica',
   GEHIS_ESTALE  VARCHAR(255)  NOT NULL COMMENT 'EstadoAlergiasHistoriaClinica',
   GEPAC_CODIGO  CHAR(10)      NOT NULL COMMENT 'CodigoPaciente: Clave foránea hacia PACIENTE (relación historia).',
   CONSTRAINT PK_GEHIS PRIMARY KEY (GEHIS_CODIGO),
   CONSTRAINT AK_GEHIS_1 UNIQUE (GEHIS_NUMERO),
   CONSTRAINT AK_GEHIS_2 UNIQUE (GEPAC_CODIGO)
)
ENGINE = InnoDB
COMMENT = 'HISTORIA_CLINICA. Entidad utilizada para gestionar la historia clínica de cada paciente.';

/*==============================================================*/
/* Tabla: GEEPI_EPISOD (EPISODIO_CLINICO)                       */
/*==============================================================*/

CREATE TABLE GEEPI_EPISOD
(
   GEEPI_CODIGO  CHAR(10)      NOT NULL COMMENT 'CodigoEpisodioClinico',
   GEEPI_TIPO    VARCHAR(255)  NOT NULL COMMENT 'TipoEpisodioClinico',
   GEEPI_INGRES  DATETIME      NOT NULL COMMENT 'IngresoEpisodioClinico',
   GEEPI_INIATE  DATETIME      NULL     COMMENT 'InicioAtencionEpisodioClinico',
   GEEPI_CIERRE  DATETIME      NULL     COMMENT 'CierreEpisodioClinico',
   GEEPI_ESTADO  VARCHAR(255)  NOT NULL COMMENT 'EstadoEpisodioClinico',
   GEEPI_DISPOS  VARCHAR(255)  NULL     COMMENT 'DisposicionEpisodioClinico',
   GEEPI_MOTIVO  TEXT          NULL     COMMENT 'MotivoEpisodioClinico',
   GEEPI_DESTIN  VARCHAR(150)  NULL     COMMENT 'DestinoEpisodioClinico',
   GEPAC_CODIGO  CHAR(10)      NOT NULL COMMENT 'CodigoPaciente: Clave foránea hacia PACIENTE (relación episodios).',
   GEHIS_CODIGO  CHAR(10)      NOT NULL COMMENT 'CodigoHistoriaClinica: Clave foránea hacia HISTORIA_CLINICA (relación cronologia).',
   GECIT_CODIGO  CHAR(10)      NULL     COMMENT 'CodigoCita: Clave foránea hacia CITA (relación originaConsulta).',
   CONSTRAINT PK_GEEPI PRIMARY KEY (GEEPI_CODIGO)
)
ENGINE = InnoDB
COMMENT = 'EPISODIO_CLINICO. Entidad utilizada para registrar cada atención del paciente en consulta externa o emergencia.';

CREATE UNIQUE INDEX UX_GEEPI_GECIT ON GEEPI_EPISOD (GECIT_CODIGO);

CREATE INDEX IX_GEEPI_GEPAC ON GEEPI_EPISOD (GEPAC_CODIGO);

CREATE INDEX IX_GEEPI_GEHIS ON GEEPI_EPISOD (GEHIS_CODIGO);

/*==============================================================*/
/* Tabla: GENOT_NOTACL (NOTA_CLINICA)                           */
/*==============================================================*/

CREATE TABLE GENOT_NOTACL
(
   GENOT_CODIGO  CHAR(12)      NOT NULL COMMENT 'CodigoNotaClinica',
   GENOT_TIPO    VARCHAR(255)  NOT NULL COMMENT 'TipoNotaClinica',
   GENOT_CONTEN  TEXT          NOT NULL COMMENT 'ContenidoNotaClinica',
   GENOT_CREADA  DATETIME      NOT NULL COMMENT 'CreadaNotaClinica',
   GENOT_FINALI  DATETIME      NULL     COMMENT 'FinalizadaNotaClinica',
   GENOT_ESTADO  VARCHAR(255)  NOT NULL COMMENT 'EstadoNotaClinica',
   GEEPI_CODIGO  CHAR(10)      NOT NULL COMMENT 'CodigoEpisodioClinico: Clave foránea hacia EPISODIO_CLINICO (relación notas).',
   PEPER_CODIGO  CHAR(6)       NOT NULL COMMENT 'CodigoPersona: Clave foránea hacia PERSONA (relación autorNota).',
   CONSTRAINT PK_GENOT PRIMARY KEY (GENOT_CODIGO)
)
ENGINE = InnoDB
COMMENT = 'NOTA_CLINICA. Entidad utilizada para registrar las notas clínicas de cada atención.';

CREATE INDEX IX_GENOT_GEEPI ON GENOT_NOTACL (GEEPI_CODIGO);

CREATE INDEX IX_GENOT_PEPER ON GENOT_NOTACL (PEPER_CODIGO);

/*==============================================================*/
/* Tabla: GEADE_ADENDA (ADENDA_CLINICA)                         */
/*==============================================================*/

CREATE TABLE GEADE_ADENDA
(
   GEADE_CODIGO  CHAR(10)  NOT NULL COMMENT 'CodigoAdendaClinica',
   GEADE_INSTAN  DATETIME  NOT NULL COMMENT 'InstanteAdendaClinica',
   GEADE_CONTEN  TEXT      NOT NULL COMMENT 'ContenidoAdendaClinica',
   GEADE_MOTIVO  TEXT      NOT NULL COMMENT 'MotivoAdendaClinica',
   GENOT_CODIGO  CHAR(12)  NOT NULL COMMENT 'CodigoNotaClinica: Clave foránea hacia NOTA_CLINICA (relación adendas).',
   PEPER_CODIGO  CHAR(6)   NOT NULL COMMENT 'CodigoPersona: Clave foránea hacia PERSONA (relación autorAdenda).',
   CONSTRAINT PK_GEADE PRIMARY KEY (GEADE_CODIGO)
)
ENGINE = InnoDB
COMMENT = 'ADENDA_CLINICA. Entidad utilizada para registrar las correcciones de una nota clínica finalizada.';

CREATE INDEX IX_GEADE_GENOT ON GEADE_ADENDA (GENOT_CODIGO);

CREATE INDEX IX_GEADE_PEPER ON GEADE_ADENDA (PEPER_CODIGO);

/*==============================================================*/
/* Tabla: GESIG_SIGNOV (SIGNO_VITAL)                            */
/*==============================================================*/

CREATE TABLE GESIG_SIGNOV
(
   GESIG_CODIGO  CHAR(12)       NOT NULL COMMENT 'CodigoSignoVital',
   GESIG_TIPO    VARCHAR(255)   NOT NULL COMMENT 'TipoSignoVital',
   GESIG_VALOR   DECIMAL(18,4)  NOT NULL COMMENT 'ValorSignoVital',
   GESIG_UNIDAD  VARCHAR(255)   NOT NULL COMMENT 'UnidadSignoVital',
   GESIG_INSTAN  DATETIME       NOT NULL COMMENT 'InstanteSignoVital',
   GESIG_OBSERV  TEXT           NULL     COMMENT 'ObservacionSignoVital',
   GEEPI_CODIGO  CHAR(10)       NOT NULL COMMENT 'CodigoEpisodioClinico: Clave foránea hacia EPISODIO_CLINICO (relación signos).',
   PEPER_CODIGO  CHAR(6)        NOT NULL COMMENT 'CodigoPersona: Clave foránea hacia PERSONA (relación registraSigno).',
   CONSTRAINT PK_GESIG PRIMARY KEY (GESIG_CODIGO)
)
ENGINE = InnoDB
COMMENT = 'SIGNO_VITAL. Entidad utilizada para registrar los signos vitales del paciente.';

CREATE INDEX IX_GESIG_GEEPI ON GESIG_SIGNOV (GEEPI_CODIGO);

CREATE INDEX IX_GESIG_PEPER ON GESIG_SIGNOV (PEPER_CODIGO);

/*==============================================================*/
/* Tabla: GEANT_ANTECE (ANTECEDENTE)                            */
/*==============================================================*/

CREATE TABLE GEANT_ANTECE
(
   GEANT_CODIGO         CHAR(10)      NOT NULL COMMENT 'CodigoAntecedente',
   GEANT_TIPO           VARCHAR(255)  NOT NULL COMMENT 'TipoAntecedente',
   GEANT_DESCRI         TEXT          NOT NULL COMMENT 'DescripcionAntecedente',
   GEANT_VIGENT         BOOL          NOT NULL COMMENT 'VigenteAntecedente',
   GEANT_INSTAN         DATETIME      NOT NULL COMMENT 'InstanteAntecedente',
   GEANT_MOTREC         VARCHAR(255)  NULL     COMMENT 'MotivoRectificacionAntecedente',
   GEANT_FUENTE         VARCHAR(200)  NOT NULL COMMENT 'FuenteAntecedente',
   GEANT_VERIFI         VARCHAR(40)   NOT NULL COMMENT 'VerificacionAntecedente',
   GEANT_OBSERV         TEXT          NULL     COMMENT 'ObservacionesAntecedente',
   GEANT_ESTADO         VARCHAR(40)   NOT NULL COMMENT 'EstadoAntecedente',
   GEHIS_CODIGO         CHAR(10)      NOT NULL COMMENT 'CodigoHistoriaClinica: Clave foránea hacia HISTORIA_CLINICA (relación antecedentes).',
   GEANT_CODIGO_RECTIF  CHAR(10)      NULL     COMMENT 'CodigoAntecedenteRectificado: Clave foránea hacia ANTECEDENTE (relación rectificaciones).',
   PEPER_CODIGO         CHAR(6)       NOT NULL COMMENT 'CodigoPersona: Clave foránea hacia PERSONA (relación autorAntecedente).',
   CONSTRAINT PK_GEANT PRIMARY KEY (GEANT_CODIGO)
)
ENGINE = InnoDB
COMMENT = 'ANTECEDENTE. Entidad utilizada para registrar los antecedentes médicos del paciente.';

CREATE INDEX IX_GEANT_GEHIS ON GEANT_ANTECE (GEHIS_CODIGO);

CREATE INDEX IX_GEANT_GEANT_RECTIF ON GEANT_ANTECE (GEANT_CODIGO_RECTIF);

CREATE INDEX IX_GEANT_PEPER ON GEANT_ANTECE (PEPER_CODIGO);

/*==============================================================*/
/* Tabla: GEALE_ALERGI (ALERGIA)                                */
/*==============================================================*/

CREATE TABLE GEALE_ALERGI
(
   GEALE_CODIGO         CHAR(10)      NOT NULL COMMENT 'CodigoAlergia',
   GEALE_SUSTAN         VARCHAR(255)  NOT NULL COMMENT 'SustanciaAlergia',
   GEALE_REACCI         VARCHAR(255)  NOT NULL COMMENT 'ReaccionAlergia',
   GEALE_ESTADO         VARCHAR(255)  NOT NULL COMMENT 'EstadoAlergia',
   GEALE_INSTAN         DATETIME      NOT NULL COMMENT 'InstanteAlergia',
   GEALE_MOTREC         VARCHAR(255)  NULL     COMMENT 'MotivoRectificacionAlergia',
   GEALE_FUENTE         VARCHAR(200)  NOT NULL COMMENT 'FuenteAlergia',
   GEALE_VERIFI         VARCHAR(40)   NOT NULL COMMENT 'VerificacionAlergia',
   GEALE_OBSERV         TEXT          NULL     COMMENT 'ObservacionesAlergia',
   GEALE_TIPO           VARCHAR(50)   NOT NULL COMMENT 'TipoAlergia',
   GEALE_DESCRI         TEXT          NOT NULL COMMENT 'DescripcionAlergia',
   GEHIS_CODIGO         CHAR(10)      NOT NULL COMMENT 'CodigoHistoriaClinica: Clave foránea hacia HISTORIA_CLINICA (relación alergias).',
   GEALE_CODIGO_RECTIF  CHAR(10)      NULL     COMMENT 'CodigoAlergiaRectificada: Clave foránea hacia ALERGIA (relación rectificaciones).',
   PEPER_CODIGO         CHAR(6)       NOT NULL COMMENT 'CodigoPersona: Clave foránea hacia PERSONA (relación autorAlergia).',
   CONSTRAINT PK_GEALE PRIMARY KEY (GEALE_CODIGO)
)
ENGINE = InnoDB
COMMENT = 'ALERGIA. Entidad utilizada para registrar las alergias del paciente.';

CREATE INDEX IX_GEALE_GEHIS ON GEALE_ALERGI (GEHIS_CODIGO);

CREATE INDEX IX_GEALE_GEALE_RECTIF ON GEALE_ALERGI (GEALE_CODIGO_RECTIF);

CREATE INDEX IX_GEALE_PEPER ON GEALE_ALERGI (PEPER_CODIGO);

/*==============================================================*/
/* Tabla: GEDIA_DIAGNO (DIAGNOSTICO)                            */
/*==============================================================*/

CREATE TABLE GEDIA_DIAGNO
(
   GEDIA_CODIGO  CHAR(12)      NOT NULL COMMENT 'CodigoDiagnostico',
   GEDIA_CODCIE  CHAR(6)       NOT NULL COMMENT 'CodigoCIEDiagnostico',
   GEDIA_DESCRI  TEXT          NOT NULL COMMENT 'DescripcionDiagnostico',
   GEDIA_TIPO    VARCHAR(255)  NOT NULL COMMENT 'TipoDiagnostico',
   GEDIA_INSTAN  DATETIME      NOT NULL COMMENT 'InstanteDiagnostico',
   GEEPI_CODIGO  CHAR(10)      NOT NULL COMMENT 'CodigoEpisodioClinico: Clave foránea hacia EPISODIO_CLINICO (relación diagnosticos).',
   PEPER_CODIGO  CHAR(6)       NOT NULL COMMENT 'CodigoPersona: Clave foránea hacia PERSONA (relación medicoDiagnostico).',
   CONSTRAINT PK_GEDIA PRIMARY KEY (GEDIA_CODIGO)
)
ENGINE = InnoDB
COMMENT = 'DIAGNOSTICO. Entidad utilizada para registrar los diagnósticos realizados al paciente.';

CREATE INDEX IX_GEDIA_GEEPI ON GEDIA_DIAGNO (GEEPI_CODIGO);

CREATE INDEX IX_GEDIA_PEPER ON GEDIA_DIAGNO (PEPER_CODIGO);

/*==============================================================*/
/* Tabla: GEPRE_PRESCR (PRESCRIPCION)                           */
/*==============================================================*/

CREATE TABLE GEPRE_PRESCR
(
   GEPRE_CODIGO  CHAR(10)      NOT NULL COMMENT 'CodigoPrescripcion',
   GEPRE_EMITID  DATETIME      NOT NULL COMMENT 'EmitidaPrescripcion',
   GEPRE_ESTADO  VARCHAR(255)  NOT NULL COMMENT 'EstadoPrescripcion',
   GEPRE_MOTANU  VARCHAR(255)  NULL     COMMENT 'MotivoAnulacionPrescripcion',
   GEEPI_CODIGO  CHAR(10)      NOT NULL COMMENT 'CodigoEpisodioClinico: Clave foránea hacia EPISODIO_CLINICO (relación prescripciones).',
   PEPER_CODIGO  CHAR(6)       NOT NULL COMMENT 'CodigoPersona: Clave foránea hacia PERSONA (relación prescriptor).',
   CONSTRAINT PK_GEPRE PRIMARY KEY (GEPRE_CODIGO)
)
ENGINE = InnoDB
COMMENT = 'PRESCRIPCION. Entidad utilizada para registrar las recetas médicas emitidas al paciente.';

CREATE INDEX IX_GEPRE_GEEPI ON GEPRE_PRESCR (GEEPI_CODIGO);

CREATE INDEX IX_GEPRE_PEPER ON GEPRE_PRESCR (PEPER_CODIGO);

/*==============================================================*/
/* Tabla: GEDET_DETALL (DETALLE_PRESCRIPCION)                   */
/*==============================================================*/

CREATE TABLE GEDET_DETALL
(
   GEDET_CODIGO  CHAR(12)       NOT NULL COMMENT 'CodigoDetallePrescripcion',
   GEDET_DOSIS   VARCHAR(255)   NOT NULL COMMENT 'DosisDetallePrescripcion',
   GEDET_VIA     VARCHAR(255)   NOT NULL COMMENT 'ViaDetallePrescripcion',
   GEDET_FRECUE  VARCHAR(255)   NOT NULL COMMENT 'FrecuenciaDetallePrescripcion',
   GEDET_DURACI  VARCHAR(255)   NOT NULL COMMENT 'DuracionDetallePrescripcion',
   GEDET_CANTID  DECIMAL(18,4)  NOT NULL COMMENT 'CantidadDetallePrescripcion',
   GEDET_PENDIE  DECIMAL(18,4)  NOT NULL COMMENT 'PendienteDetallePrescripcion',
   GEDET_INSTRU  TEXT           NULL     COMMENT 'InstruccionesDetallePrescripcion',
   GEDET_JUSALT  TEXT           NULL     COMMENT 'JustificacionAlertaDetallePrescripcion',
   GEPRE_CODIGO  CHAR(10)       NOT NULL COMMENT 'CodigoPrescripcion: Clave foránea hacia PRESCRIPCION (relación items).',
   GEMED_CODIGO  CHAR(6)        NOT NULL COMMENT 'CodigoMedicamento: Clave foránea hacia MEDICAMENTO (relación medicamentoPrescrito).',
   CONSTRAINT PK_GEDET PRIMARY KEY (GEDET_CODIGO)
)
ENGINE = InnoDB
COMMENT = 'DETALLE_PRESCRIPCION. Entidad utilizada para registrar los medicamentos de cada receta.';

CREATE INDEX IX_GEDET_GEPRE ON GEDET_DETALL (GEPRE_CODIGO);

CREATE INDEX IX_GEDET_GEMED ON GEDET_DETALL (GEMED_CODIGO);

/*==============================================================*/
/* Tabla: GECUI_CUIDAD (CUIDADO_ENFERMERIA)                     */
/*==============================================================*/

CREATE TABLE GECUI_CUIDAD
(
   GECUI_CODIGO  CHAR(12)      NOT NULL COMMENT 'CodigoCuidadoEnfermeria',
   GECUI_ACTIVI  VARCHAR(255)  NOT NULL COMMENT 'ActividadCuidadoEnfermeria',
   GECUI_REALIZ  DATETIME      NOT NULL COMMENT 'RealizadaCuidadoEnfermeria',
   GECUI_OBSERV  TEXT          NULL     COMMENT 'ObservacionCuidadoEnfermeria',
   GECUI_ESTADO  VARCHAR(255)  NOT NULL COMMENT 'EstadoCuidadoEnfermeria',
   GEEPI_CODIGO  CHAR(10)      NOT NULL COMMENT 'CodigoEpisodioClinico: Clave foránea hacia EPISODIO_CLINICO (relación cuidados).',
   GENOT_CODIGO  CHAR(12)      NOT NULL COMMENT 'CodigoNotaClinica: Clave foránea hacia NOTA_CLINICA (relación documentaCuidado).',
   PEPER_CODIGO  CHAR(6)       NOT NULL COMMENT 'CodigoPersona: Clave foránea hacia PERSONA (relación enfermero).',
   CONSTRAINT PK_GECUI PRIMARY KEY (GECUI_CODIGO),
   CONSTRAINT AK_GECUI_1 UNIQUE (GENOT_CODIGO)
)
ENGINE = InnoDB
COMMENT = 'CUIDADO_ENFERMERIA. Entidad utilizada para registrar los cuidados de enfermería brindados al paciente.';

CREATE INDEX IX_GECUI_GEEPI ON GECUI_CUIDAD (GEEPI_CODIGO);

CREATE INDEX IX_GECUI_PEPER ON GECUI_CUIDAD (PEPER_CODIGO);

/*==============================================================*/
/* Tabla: GETRI_TRIAJE (TRIAJE)                                 */
/*==============================================================*/

CREATE TABLE GETRI_TRIAJE
(
   GETRI_CODIGO  CHAR(10)      NOT NULL COMMENT 'CodigoTriaje',
   GETRI_CATEGO  VARCHAR(255)  NOT NULL COMMENT 'CategoriaTriaje',
   GETRI_INSTAN  DATETIME      NOT NULL COMMENT 'InstanteTriaje',
   GETRI_PROTOC  VARCHAR(255)  NOT NULL COMMENT 'ProtocoloTriaje',
   GETRI_OBSERV  TEXT          NULL     COMMENT 'ObservacionTriaje',
   GEEPI_CODIGO  CHAR(10)      NOT NULL COMMENT 'CodigoEpisodioClinico: Clave foránea hacia EPISODIO_CLINICO (relación clasificaciones).',
   PEPER_CODIGO  CHAR(6)       NOT NULL COMMENT 'CodigoPersona: Clave foránea hacia PERSONA (relación clasificador).',
   CONSTRAINT PK_GETRI PRIMARY KEY (GETRI_CODIGO)
)
ENGINE = InnoDB
COMMENT = 'TRIAJE. Entidad utilizada para registrar la clasificación del paciente en emergencia.';

CREATE INDEX IX_GETRI_GEEPI ON GETRI_TRIAJE (GEEPI_CODIGO);

CREATE INDEX IX_GETRI_PEPER ON GETRI_TRIAJE (PEPER_CODIGO);

/*==============================================================*/
/* Tabla: GEEXA_EXAMEN (EXAMEN_LABORATORIO)                     */
/*==============================================================*/

CREATE TABLE GEEXA_EXAMEN
(
   GEEXA_CODIGO  CHAR(4)       NOT NULL COMMENT 'CodigoExamenLaboratorio',
   GEEXA_CODEXT  CHAR(10)      NOT NULL COMMENT 'CodigoExternoExamenLaboratorio',
   GEEXA_NOMBRE  VARCHAR(255)  NOT NULL COMMENT 'NombreExamenLaboratorio',
   GEEXA_UNIDAD  VARCHAR(255)  NOT NULL COMMENT 'UnidadExamenLaboratorio',
   GEEXA_CONFIG  TEXT          NOT NULL COMMENT 'ConfiguracionExamenLaboratorio',
   GEEXA_ACTIVO  BOOL          NOT NULL COMMENT 'ActivoExamenLaboratorio',
   GEEXA_RANREF  VARCHAR(200)  NULL     COMMENT 'RangoReferenciaExamenLaboratorio',
   CONSTRAINT PK_GEEXA PRIMARY KEY (GEEXA_CODIGO)
)
ENGINE = InnoDB
COMMENT = 'EXAMEN_LABORATORIO. Entidad utilizada para gestionar el catálogo de exámenes de laboratorio.';

/*==============================================================*/
/* Tabla: GEORD_ORDENL (ORDEN_LABORATORIO)                      */
/*==============================================================*/

CREATE TABLE GEORD_ORDENL
(
   GEORD_CODIGO  CHAR(10)      NOT NULL COMMENT 'CodigoOrdenLaboratorio',
   GEORD_SOLICI  DATETIME      NOT NULL COMMENT 'SolicitadaOrdenLaboratorio',
   GEORD_PRIORI  VARCHAR(255)  NOT NULL COMMENT 'PrioridadOrdenLaboratorio',
   GEORD_ESTADO  VARCHAR(255)  NOT NULL COMMENT 'EstadoOrdenLaboratorio',
   GEORD_MOTIVO  TEXT          NOT NULL COMMENT 'MotivoOrdenLaboratorio',
   GEEPI_CODIGO  CHAR(10)      NOT NULL COMMENT 'CodigoEpisodioClinico: Clave foránea hacia EPISODIO_CLINICO (relación ordenesLaboratorio).',
   PEPER_CODIGO  CHAR(6)       NOT NULL COMMENT 'CodigoPersona: Clave foránea hacia PERSONA (relación solicitanteLaboratorio).',
   CONSTRAINT PK_GEORD PRIMARY KEY (GEORD_CODIGO)
)
ENGINE = InnoDB
COMMENT = 'ORDEN_LABORATORIO. Entidad utilizada para registrar las órdenes de laboratorio del paciente.';

CREATE INDEX IX_GEORD_GEEPI ON GEORD_ORDENL (GEEPI_CODIGO);

CREATE INDEX IX_GEORD_PEPER ON GEORD_ORDENL (PEPER_CODIGO);

/*==============================================================*/
/* Tabla: GEMUE_MUESTR (MUESTRA_LABORATORIO)                    */
/*==============================================================*/

CREATE TABLE GEMUE_MUESTR
(
   GEMUE_CODIGO  CHAR(12)      NOT NULL COMMENT 'CodigoMuestraLaboratorio',
   GEMUE_CODEXT  CHAR(15)      NOT NULL COMMENT 'CodigoExternoMuestraLaboratorio',
   GEMUE_TIPO    VARCHAR(255)  NOT NULL COMMENT 'TipoMuestraLaboratorio',
   GEMUE_RECIBI  DATETIME      NULL     COMMENT 'RecibidaMuestraLaboratorio',
   GEMUE_ESTADO  VARCHAR(255)  NOT NULL COMMENT 'EstadoMuestraLaboratorio',
   GEMUE_INCIDE  VARCHAR(255)  NULL     COMMENT 'IncidenciaMuestraLaboratorio',
   GEORD_CODIGO  CHAR(10)      NOT NULL COMMENT 'CodigoOrdenLaboratorio: Clave foránea hacia ORDEN_LABORATORIO (relación muestras).',
   PEPER_CODIGO  CHAR(6)       NOT NULL COMMENT 'CodigoPersona: Clave foránea hacia PERSONA (relación recibeMuestra).',
   CONSTRAINT PK_GEMUE PRIMARY KEY (GEMUE_CODIGO),
   CONSTRAINT AK_GEMUE_1 UNIQUE (GEMUE_CODEXT)
)
ENGINE = InnoDB
COMMENT = 'MUESTRA_LABORATORIO. Entidad utilizada para registrar las muestras tomadas al paciente.';

CREATE INDEX IX_GEMUE_GEORD ON GEMUE_MUESTR (GEORD_CODIGO);

CREATE INDEX IX_GEMUE_PEPER ON GEMUE_MUESTR (PEPER_CODIGO);

/*==============================================================*/
/* Tabla: GERES_RESULT (RESULTADO_LABORATORIO)                  */
/*==============================================================*/

CREATE TABLE GERES_RESULT
(
   GERES_CODIGO  CHAR(12)      NOT NULL COMMENT 'CodigoResultadoLaboratorio',
   GERES_REGIST  DATETIME      NOT NULL COMMENT 'RegistradoResultadoLaboratorio',
   GERES_ESTADO  VARCHAR(255)  NOT NULL COMMENT 'EstadoResultadoLaboratorio',
   GERES_VALOR   VARCHAR(255)  NULL     COMMENT 'ValorResultadoLaboratorio',
   GERES_UNIDAD  VARCHAR(255)  NULL     COMMENT 'UnidadResultadoLaboratorio',
   GERES_RANREF  VARCHAR(200)  NULL     COMMENT 'RangoReferenciaResultadoLaboratorio',
   GEDOL_CODIGO  CHAR(12)      NOT NULL COMMENT 'CodigoDetalleOrdenLaboratorio: Clave foránea hacia DETALLE_ORDEN_LABORATORIO (relación resultado).',
   GEMUE_CODIGO  CHAR(12)      NOT NULL COMMENT 'CodigoMuestraLaboratorio: Clave foránea hacia MUESTRA_LABORATORIO (relación procesamiento).',
   PEPER_CODIGO  CHAR(6)       NOT NULL COMMENT 'CodigoPersona: Clave foránea hacia PERSONA (relación registraResultado).',
   CONSTRAINT PK_GERES PRIMARY KEY (GERES_CODIGO),
   CONSTRAINT AK_GERES_1 UNIQUE (GEDOL_CODIGO)
)
ENGINE = InnoDB
COMMENT = 'RESULTADO_LABORATORIO. Entidad utilizada para registrar los resultados de los exámenes de laboratorio.';

CREATE INDEX IX_GERES_GEMUE ON GERES_RESULT (GEMUE_CODIGO);

CREATE INDEX IX_GERES_PEPER ON GERES_RESULT (PEPER_CODIGO);

/*==============================================================*/
/* Tabla: GEVER_VERSIO (VERSION_RESULTADO)                      */
/*==============================================================*/

CREATE TABLE GEVER_VERSIO
(
   GEVER_CODIGO  CHAR(12)      NOT NULL COMMENT 'CodigoVersionResultado',
   GEVER_NUMERO  INT           NOT NULL COMMENT 'NumeroVersionResultado',
   GEVER_VALOR   VARCHAR(255)  NOT NULL COMMENT 'ValorVersionResultado',
   GEVER_INSTAN  DATETIME      NOT NULL COMMENT 'InstanteVersionResultado',
   GEVER_MOTIVO  TEXT          NULL     COMMENT 'MotivoVersionResultado',
   GEVER_ESTADO  VARCHAR(255)  NOT NULL COMMENT 'EstadoVersionResultado',
   GEVER_VALIDA  DATETIME      NULL     COMMENT 'ValidadaVersionResultado',
   GERES_CODIGO  CHAR(12)      NOT NULL COMMENT 'CodigoResultadoLaboratorio: Clave foránea hacia RESULTADO_LABORATORIO (relación versiones).',
   PEPER_CODIGO  CHAR(6)       NOT NULL COMMENT 'CodigoPersona: Clave foránea hacia PERSONA (relación validadorResultado).',
   CONSTRAINT PK_GEVER PRIMARY KEY (GEVER_CODIGO)
)
ENGINE = InnoDB
COMMENT = 'VERSION_RESULTADO. Entidad utilizada para registrar las versiones corregidas de un resultado de laboratorio.';

CREATE INDEX IX_GEVER_GERES ON GEVER_VERSIO (GERES_CODIGO);

CREATE INDEX IX_GEVER_PEPER ON GEVER_VERSIO (PEPER_CODIGO);

/*==============================================================*/
/* Tabla: GECOM_COMUNI (COMUNICACION_CRITICA)                   */
/*==============================================================*/

CREATE TABLE GECOM_COMUNI
(
   GECOM_CODIGO  CHAR(10)      NOT NULL COMMENT 'CodigoComunicacionCritica',
   GECOM_INSTAN  DATETIME      NOT NULL COMMENT 'InstanteComunicacionCritica',
   GECOM_DESTIN  VARCHAR(255)  NOT NULL COMMENT 'DestinatarioComunicacionCritica',
   GECOM_MEDIO   VARCHAR(255)  NOT NULL COMMENT 'MedioComunicacionCritica',
   GECOM_ESTADO  VARCHAR(255)  NOT NULL COMMENT 'EstadoComunicacionCritica',
   GECOM_EVIDEN  TEXT          NOT NULL COMMENT 'EvidenciaComunicacionCritica',
   GECOM_SEGUIM  TEXT          NULL     COMMENT 'SeguimientoComunicacionCritica',
   GERES_CODIGO  CHAR(12)      NOT NULL COMMENT 'CodigoResultadoLaboratorio: Clave foránea hacia RESULTADO_LABORATORIO (relación comunicaciones).',
   PEPER_CODIGO  CHAR(6)       NOT NULL COMMENT 'CodigoPersona: Clave foránea hacia PERSONA (relación comunicaCritico).',
   CONSTRAINT PK_GECOM PRIMARY KEY (GECOM_CODIGO)
)
ENGINE = InnoDB
COMMENT = 'COMUNICACION_CRITICA. Entidad utilizada para registrar la comunicación de resultados críticos al médico.';

CREATE INDEX IX_GECOM_GERES ON GECOM_COMUNI (GERES_CODIGO);

CREATE INDEX IX_GECOM_PEPER ON GECOM_COMUNI (PEPER_CODIGO);

/*==============================================================*/
/* Tabla: GEMED_MEDICA (MEDICAMENTO)                            */
/*==============================================================*/

CREATE TABLE GEMED_MEDICA
(
   GEMED_CODIGO  CHAR(6)        NOT NULL COMMENT 'CodigoMedicamento',
   GEMED_CODEXT  CHAR(20)       NOT NULL COMMENT 'CodigoExternoMedicamento',
   GEMED_NOMBRE  VARCHAR(255)   NOT NULL COMMENT 'NombreMedicamento',
   GEMED_PRESEN  VARCHAR(255)   NOT NULL COMMENT 'PresentacionMedicamento',
   GEMED_STOMIN  DECIMAL(18,4)  NOT NULL COMMENT 'StockMinimoMedicamento',
   GEMED_CONESP  BOOL           NOT NULL COMMENT 'ControlEspecialMedicamento',
   GEMED_ACTIVO  BOOL           NOT NULL COMMENT 'ActivoMedicamento',
   GEMED_PRIACT  VARCHAR(150)   NOT NULL COMMENT 'PrincipioActivoMedicamento',
   GEMED_CONCEN  VARCHAR(100)   NOT NULL COMMENT 'ConcentracionMedicamento',
   GEMED_UNIDAD  VARCHAR(40)    NOT NULL COMMENT 'UnidadMedicamento',
   CONSTRAINT PK_GEMED PRIMARY KEY (GEMED_CODIGO)
)
ENGINE = InnoDB
COMMENT = 'MEDICAMENTO. Entidad utilizada para gestionar el catálogo de medicamentos de farmacia.';

/*==============================================================*/
/* Tabla: GEPRO_PROVEE (PROVEEDOR)                              */
/*==============================================================*/

CREATE TABLE GEPRO_PROVEE
(
   GEPRO_CODIGO  CHAR(4)       NOT NULL COMMENT 'CodigoProveedor',
   GEPRO_IDENTI  VARCHAR(255)  NOT NULL COMMENT 'IdentificacionProveedor',
   GEPRO_NOMBRE  VARCHAR(255)  NOT NULL COMMENT 'NombreProveedor',
   GEPRO_CONTAC  VARCHAR(255)  NULL     COMMENT 'ContactoProveedor',
   GEPRO_ACTIVO  BOOL          NOT NULL COMMENT 'ActivoProveedor',
   CONSTRAINT PK_GEPRO PRIMARY KEY (GEPRO_CODIGO)
)
ENGINE = InnoDB
COMMENT = 'PROVEEDOR. Entidad utilizada para gestionar los proveedores de medicamentos.';

/*==============================================================*/
/* Tabla: GELOT_LOTEME (LOTE_MEDICAMENTO)                       */
/*==============================================================*/

CREATE TABLE GELOT_LOTEME
(
   GELOT_CODIGO  CHAR(10)       NOT NULL COMMENT 'CodigoLoteMedicamento',
   GELOT_NUMERO  VARCHAR(255)   NOT NULL COMMENT 'NumeroLoteMedicamento',
   GELOT_CADUCA  DATE           NOT NULL COMMENT 'CaducaLoteMedicamento',
   GELOT_EXISTE  DECIMAL(18,4)  NOT NULL COMMENT 'ExistenciaLoteMedicamento',
   GELOT_ESTADO  VARCHAR(255)   NOT NULL COMMENT 'EstadoLoteMedicamento',
   GEMED_CODIGO  CHAR(6)        NOT NULL COMMENT 'CodigoMedicamento: Clave foránea hacia MEDICAMENTO (relación lotes).',
   GEPRO_CODIGO  CHAR(4)        NOT NULL COMMENT 'CodigoProveedor: Clave foránea hacia PROVEEDOR (relación suministro).',
   CONSTRAINT PK_GELOT PRIMARY KEY (GELOT_CODIGO)
)
ENGINE = InnoDB
COMMENT = 'LOTE_MEDICAMENTO. Entidad utilizada para gestionar los lotes de cada medicamento.';

CREATE INDEX IX_GELOT_GEMED ON GELOT_LOTEME (GEMED_CODIGO);

CREATE INDEX IX_GELOT_GEPRO ON GELOT_LOTEME (GEPRO_CODIGO);

/*==============================================================*/
/* Tabla: GEMOV_MOVIMI (MOVIMIENTO_INVENTARIO)                  */
/*==============================================================*/

CREATE TABLE GEMOV_MOVIMI
(
   GEMOV_CODIGO  CHAR(12)       NOT NULL COMMENT 'CodigoMovimientoInventario',
   GEMOV_TIPO    VARCHAR(255)   NOT NULL COMMENT 'TipoMovimientoInventario',
   GEMOV_CANTID  DECIMAL(18,4)  NOT NULL COMMENT 'CantidadMovimientoInventario',
   GEMOV_INSTAN  DATETIME       NOT NULL COMMENT 'InstanteMovimientoInventario',
   GEMOV_MOTIVO  TEXT           NOT NULL COMMENT 'MotivoMovimientoInventario',
   GEMOV_CLAOPE  VARCHAR(255)   NOT NULL COMMENT 'ClaveOperacionMovimientoInventario',
   GEMOV_SALRES  DECIMAL(18,4)  NOT NULL COMMENT 'SaldoResultanteMovimientoInventario',
   GELOT_CODIGO  CHAR(10)       NOT NULL COMMENT 'CodigoLoteMedicamento: Clave foránea hacia LOTE_MEDICAMENTO (relación movimientos).',
   PEPER_CODIGO  CHAR(6)        NOT NULL COMMENT 'CodigoPersona: Clave foránea hacia PERSONA (relación responsableMovimiento).',
   GEDEV_CODIGO  CHAR(8)        NULL     COMMENT 'CodigoDevolucionFarmacia: Clave foránea hacia DEVOLUCION_FARMACIA (relación entradaAprobada).',
   CONSTRAINT PK_GEMOV PRIMARY KEY (GEMOV_CODIGO),
   CONSTRAINT AK_GEMOV_1 UNIQUE (GEMOV_CLAOPE)
)
ENGINE = InnoDB
COMMENT = 'MOVIMIENTO_INVENTARIO. Entidad utilizada para registrar las entradas y salidas del inventario de farmacia.';

CREATE UNIQUE INDEX UX_GEMOV_GEDEV ON GEMOV_MOVIMI (GEDEV_CODIGO);

CREATE INDEX IX_GEMOV_GELOT ON GEMOV_MOVIMI (GELOT_CODIGO);

CREATE INDEX IX_GEMOV_PEPER ON GEMOV_MOVIMI (PEPER_CODIGO);

/*==============================================================*/
/* Tabla: GEDSP_DISPEN (DISPENSACION)                           */
/*==============================================================*/

CREATE TABLE GEDSP_DISPEN
(
   GEDSP_CODIGO  CHAR(10)      NOT NULL COMMENT 'CodigoDispensacion',
   GEDSP_INSTAN  DATETIME      NOT NULL COMMENT 'InstanteDispensacion',
   GEDSP_ESTADO  VARCHAR(255)  NOT NULL COMMENT 'EstadoDispensacion',
   GEDSP_CLAOPE  VARCHAR(255)  NOT NULL COMMENT 'ClaveOperacionDispensacion',
   GEPRE_CODIGO  CHAR(10)      NOT NULL COMMENT 'CodigoPrescripcion: Clave foránea hacia PRESCRIPCION (relación entregas).',
   PEPER_CODIGO  CHAR(6)       NOT NULL COMMENT 'CodigoPersona: Clave foránea hacia PERSONA (relación farmaceutico).',
   CONSTRAINT PK_GEDSP PRIMARY KEY (GEDSP_CODIGO),
   CONSTRAINT AK_GEDSP_1 UNIQUE (GEDSP_CLAOPE)
)
ENGINE = InnoDB
COMMENT = 'DISPENSACION. Entidad utilizada para registrar la entrega de medicamentos al paciente.';

CREATE INDEX IX_GEDSP_GEPRE ON GEDSP_DISPEN (GEPRE_CODIGO);

CREATE INDEX IX_GEDSP_PEPER ON GEDSP_DISPEN (PEPER_CODIGO);

/*==============================================================*/
/* Tabla: GEDDI_DETALL (DETALLE_DISPENSACION)                   */
/*==============================================================*/

CREATE TABLE GEDDI_DETALL
(
   GEDDI_CODIGO  CHAR(12)       NOT NULL COMMENT 'CodigoDetalleDispensacion',
   GEDDI_CANTID  DECIMAL(18,4)  NOT NULL COMMENT 'CantidadDetalleDispensacion',
   GEDSP_CODIGO  CHAR(10)       NOT NULL COMMENT 'CodigoDispensacion: Clave foránea hacia DISPENSACION (relación detalles).',
   GEDET_CODIGO  CHAR(12)       NOT NULL COMMENT 'CodigoDetallePrescripcion: Clave foránea hacia DETALLE_PRESCRIPCION (relación itemEntregado).',
   GELOT_CODIGO  CHAR(10)       NOT NULL COMMENT 'CodigoLoteMedicamento: Clave foránea hacia LOTE_MEDICAMENTO (relación loteEntregado).',
   GEMOV_CODIGO  CHAR(12)       NOT NULL COMMENT 'CodigoMovimientoInventario: Clave foránea hacia MOVIMIENTO_INVENTARIO (relación salidaStock).',
   CONSTRAINT PK_GEDDI PRIMARY KEY (GEDDI_CODIGO),
   CONSTRAINT AK_GEDDI_1 UNIQUE (GEMOV_CODIGO)
)
ENGINE = InnoDB
COMMENT = 'DETALLE_DISPENSACION. Entidad utilizada para registrar los medicamentos y lotes de cada entrega.';

CREATE INDEX IX_GEDDI_GEDSP ON GEDDI_DETALL (GEDSP_CODIGO);

CREATE INDEX IX_GEDDI_GEDET ON GEDDI_DETALL (GEDET_CODIGO);

CREATE INDEX IX_GEDDI_GELOT ON GEDDI_DETALL (GELOT_CODIGO);

/*==============================================================*/
/* Tabla: GEDEV_DEVOLU (DEVOLUCION_FARMACIA)                    */
/*==============================================================*/

CREATE TABLE GEDEV_DEVOLU
(
   GEDEV_CODIGO  CHAR(8)        NOT NULL COMMENT 'CodigoDevolucionFarmacia',
   GEDEV_CANTID  DECIMAL(18,4)  NOT NULL COMMENT 'CantidadDevolucionFarmacia',
   GEDEV_INSTAN  DATETIME       NOT NULL COMMENT 'InstanteDevolucionFarmacia',
   GEDEV_MOTIVO  TEXT           NOT NULL COMMENT 'MotivoDevolucionFarmacia',
   GEDEV_APROBA  BOOL           NOT NULL COMMENT 'AprobadaDevolucionFarmacia',
   GEDDI_CODIGO  CHAR(12)       NOT NULL COMMENT 'CodigoDetalleDispensacion: Clave foránea hacia DETALLE_DISPENSACION (relación devoluciones).',
   PEPER_CODIGO  CHAR(6)        NULL     COMMENT 'CodigoPersona: Clave foránea hacia PERSONA (relación autorizaDevolucion).',
   CONSTRAINT PK_GEDEV PRIMARY KEY (GEDEV_CODIGO)
)
ENGINE = InnoDB
COMMENT = 'DEVOLUCION_FARMACIA. Entidad utilizada para registrar las devoluciones de medicamentos a farmacia.';

CREATE INDEX IX_GEDEV_GEDDI ON GEDEV_DEVOLU (GEDDI_CODIGO);

CREATE INDEX IX_GEDEV_PEPER ON GEDEV_DEVOLU (PEPER_CODIGO);

/*==============================================================*/
/* Tabla: GEUNI_UNIDAD (UNIDAD_HOSPITALARIA)                    */
/*==============================================================*/

CREATE TABLE GEUNI_UNIDAD
(
   GEUNI_CODIGO  CHAR(3)       NOT NULL COMMENT 'CodigoUnidadHospitalaria',
   GEUNI_CODEXT  CHAR(10)      NOT NULL COMMENT 'CodigoExternoUnidadHospitalaria',
   GEUNI_NOMBRE  VARCHAR(255)  NOT NULL COMMENT 'NombreUnidadHospitalaria',
   GEUNI_ACTIVO  BOOL          NOT NULL COMMENT 'ActivoUnidadHospitalaria',
   CONSTRAINT PK_GEUNI PRIMARY KEY (GEUNI_CODIGO)
)
ENGINE = InnoDB
COMMENT = 'UNIDAD_HOSPITALARIA. Entidad utilizada para gestionar las unidades de hospitalización.';

/*==============================================================*/
/* Tabla: GECAM_CAMA (CAMA)                                     */
/*==============================================================*/

CREATE TABLE GECAM_CAMA
(
   GECAM_CODIGO  CHAR(5)       NOT NULL COMMENT 'CodigoCama',
   GECAM_CODEXT  CHAR(10)      NOT NULL COMMENT 'CodigoExternoCama',
   GECAM_ESTADO  VARCHAR(255)  NOT NULL COMMENT 'EstadoCama',
   GECAM_HABILI  BOOL          NOT NULL COMMENT 'HabilitadaCama',
   GEUNI_CODIGO  CHAR(3)       NOT NULL COMMENT 'CodigoUnidadHospitalaria: Clave foránea hacia UNIDAD_HOSPITALARIA (relación camas).',
   CONSTRAINT PK_GECAM PRIMARY KEY (GECAM_CODIGO)
)
ENGINE = InnoDB
COMMENT = 'CAMA. Entidad utilizada para gestionar las camas de cada unidad hospitalaria.';

CREATE INDEX IX_GECAM_GEUNI ON GECAM_CAMA (GEUNI_CODIGO);

/*==============================================================*/
/* Tabla: GEOIN_ORDENI (ORDEN_INGRESO)                          */
/*==============================================================*/

CREATE TABLE GEOIN_ORDENI
(
   GEOIN_CODIGO  CHAR(10)      NOT NULL COMMENT 'CodigoOrdenIngreso',
   GEOIN_EMITID  DATETIME      NOT NULL COMMENT 'EmitidaOrdenIngreso',
   GEOIN_MOTIVO  TEXT          NOT NULL COMMENT 'MotivoOrdenIngreso',
   GEOIN_ESTADO  VARCHAR(255)  NOT NULL COMMENT 'EstadoOrdenIngreso',
   GEEPI_CODIGO  CHAR(10)      NOT NULL COMMENT 'CodigoEpisodioClinico: Clave foránea hacia EPISODIO_CLINICO (relación ordenesIngreso).',
   PEPER_CODIGO  CHAR(6)       NOT NULL COMMENT 'CodigoPersona: Clave foránea hacia PERSONA (relación medicoIngreso).',
   CONSTRAINT PK_GEOIN PRIMARY KEY (GEOIN_CODIGO)
)
ENGINE = InnoDB
COMMENT = 'ORDEN_INGRESO. Entidad utilizada para registrar las órdenes de ingreso a hospitalización.';

CREATE INDEX IX_GEOIN_GEEPI ON GEOIN_ORDENI (GEEPI_CODIGO);

CREATE INDEX IX_GEOIN_PEPER ON GEOIN_ORDENI (PEPER_CODIGO);

/*==============================================================*/
/* Tabla: GEEST_ESTANC (ESTANCIA_HOSPITALARIA)                  */
/*==============================================================*/

CREATE TABLE GEEST_ESTANC
(
   GEEST_CODIGO  CHAR(10)      NOT NULL COMMENT 'CodigoEstanciaHospitalaria',
   GEEST_ADMISI  DATETIME      NOT NULL COMMENT 'AdmisionEstanciaHospitalaria',
   GEEST_EGRESO  DATETIME      NULL     COMMENT 'EgresoEstanciaHospitalaria',
   GEEST_ESTADO  VARCHAR(255)  NOT NULL COMMENT 'EstadoEstanciaHospitalaria',
   GEOIN_CODIGO  CHAR(10)      NOT NULL COMMENT 'CodigoOrdenIngreso: Clave foránea hacia ORDEN_INGRESO (relación admision).',
   GEUNI_CODIGO  CHAR(3)       NOT NULL COMMENT 'CodigoUnidadHospitalaria: Clave foránea hacia UNIDAD_HOSPITALARIA (relación unidadAdmision).',
   PEPER_CODIGO  CHAR(6)       NOT NULL COMMENT 'CodigoPersona: Clave foránea hacia PERSONA (relación registraAdmision).',
   CONSTRAINT PK_GEEST PRIMARY KEY (GEEST_CODIGO),
   CONSTRAINT AK_GEEST_1 UNIQUE (GEOIN_CODIGO)
)
ENGINE = InnoDB
COMMENT = 'ESTANCIA_HOSPITALARIA. Entidad utilizada para registrar la estancia del paciente hospitalizado.';

CREATE INDEX IX_GEEST_GEUNI ON GEEST_ESTANC (GEUNI_CODIGO);

CREATE INDEX IX_GEEST_PEPER ON GEEST_ESTANC (PEPER_CODIGO);

/*==============================================================*/
/* Tabla: GEMCA_MOVIMI (MOVIMIENTO_CAMA)                        */
/*==============================================================*/

CREATE TABLE GEMCA_MOVIMI
(
   GEMCA_CODIGO         CHAR(12)      NOT NULL COMMENT 'CodigoMovimientoCama',
   GEMCA_TIPO           VARCHAR(255)  NOT NULL COMMENT 'TipoMovimientoCama',
   GEMCA_INICIO         DATETIME      NOT NULL COMMENT 'InicioMovimientoCama',
   GEMCA_FIN            DATETIME      NULL     COMMENT 'FinMovimientoCama',
   GEMCA_MOTIVO         TEXT          NOT NULL COMMENT 'MotivoMovimientoCama',
   GEEST_CODIGO         CHAR(10)      NOT NULL COMMENT 'CodigoEstanciaHospitalaria: Clave foránea hacia ESTANCIA_HOSPITALARIA (relación movimientos).',
   GECAM_CODIGO         CHAR(5)       NOT NULL COMMENT 'CodigoCama: Clave foránea hacia CAMA (relación ocupaciones).',
   PEPER_CODIGO         CHAR(6)       NOT NULL COMMENT 'CodigoPersona: Clave foránea hacia PERSONA (relación registraMovimientoCama).',
   GEMCA_CODIGO_ANTERI  CHAR(12)      NULL     COMMENT 'CodigoMovimientoCamaAnterior: Clave foránea hacia MOVIMIENTO_CAMA (relación trasladoAnterior).',
   CONSTRAINT PK_GEMCA PRIMARY KEY (GEMCA_CODIGO)
)
ENGINE = InnoDB
COMMENT = 'MOVIMIENTO_CAMA. Entidad utilizada para registrar la ocupación y los traslados de cama.';

CREATE UNIQUE INDEX UX_GEMCA_GEMCA_ANTERI ON GEMCA_MOVIMI (GEMCA_CODIGO_ANTERI);

CREATE INDEX IX_GEMCA_GEEST ON GEMCA_MOVIMI (GEEST_CODIGO);

CREATE INDEX IX_GEMCA_GECAM ON GEMCA_MOVIMI (GECAM_CODIGO);

CREATE INDEX IX_GEMCA_PEPER ON GEMCA_MOVIMI (PEPER_CODIGO);

/*==============================================================*/
/* Tabla: GERCA_RESERV (RESERVA_CAMA)                           */
/*==============================================================*/

CREATE TABLE GERCA_RESERV
(
   GERCA_CODIGO  CHAR(10)      NOT NULL COMMENT 'CodigoReservaCama',
   GERCA_INICIO  DATETIME      NOT NULL COMMENT 'InicioReservaCama',
   GERCA_FIN     DATETIME      NOT NULL COMMENT 'FinReservaCama',
   GERCA_ESTADO  VARCHAR(255)  NOT NULL COMMENT 'EstadoReservaCama',
   GERCA_MOTIVO  TEXT          NOT NULL COMMENT 'MotivoReservaCama',
   GECAM_CODIGO  CHAR(5)       NOT NULL COMMENT 'CodigoCama: Clave foránea hacia CAMA (relación reservasCama).',
   GEPAC_CODIGO  CHAR(10)      NOT NULL COMMENT 'CodigoPaciente: Clave foránea hacia PACIENTE (relación reservasPaciente).',
   PEPER_CODIGO  CHAR(6)       NOT NULL COMMENT 'CodigoPersona: Clave foránea hacia PERSONA (relación registraReservaCama).',
   CONSTRAINT PK_GERCA PRIMARY KEY (GERCA_CODIGO)
)
ENGINE = InnoDB
COMMENT = 'RESERVA_CAMA. Entidad utilizada para registrar las reservas de cama.';

CREATE INDEX IX_GERCA_GECAM ON GERCA_RESERV (GECAM_CODIGO);

CREATE INDEX IX_GERCA_GEPAC ON GERCA_RESERV (GEPAC_CODIGO);

CREATE INDEX IX_GERCA_PEPER ON GERCA_RESERV (PEPER_CODIGO);

/*==============================================================*/
/* Tabla: GEALT_ALTAME (ALTA_MEDICA)                            */
/*==============================================================*/

CREATE TABLE GEALT_ALTAME
(
   GEALT_CODIGO  CHAR(10)  NOT NULL COMMENT 'CodigoAltaMedica',
   GEALT_INSTAN  DATETIME  NOT NULL COMMENT 'InstanteAltaMedica',
   GEALT_INDICA  TEXT      NOT NULL COMMENT 'IndicacionesAltaMedica',
   GEEST_CODIGO  CHAR(10)  NOT NULL COMMENT 'CodigoEstanciaHospitalaria: Clave foránea hacia ESTANCIA_HOSPITALARIA (relación altaMedica).',
   PEPER_CODIGO  CHAR(6)   NOT NULL COMMENT 'CodigoPersona: Clave foránea hacia PERSONA (relación medicoAlta).',
   CONSTRAINT PK_GEALT PRIMARY KEY (GEALT_CODIGO),
   CONSTRAINT AK_GEALT_1 UNIQUE (GEEST_CODIGO)
)
ENGINE = InnoDB
COMMENT = 'ALTA_MEDICA. Entidad utilizada para registrar el alta médica del paciente.';

CREATE INDEX IX_GEALT_PEPER ON GEALT_ALTAME (PEPER_CODIGO);

/*==============================================================*/
/* Tabla: GEAAD_ALTAAD (ALTA_ADMINISTRATIVA)                    */
/*==============================================================*/

CREATE TABLE GEAAD_ALTAAD
(
   GEAAD_CODIGO  CHAR(10)      NOT NULL COMMENT 'CodigoAltaAdministrativa',
   GEAAD_INSTAN  DATETIME      NOT NULL COMMENT 'InstanteAltaAdministrativa',
   GEAAD_ESTADO  VARCHAR(255)  NOT NULL COMMENT 'EstadoAltaAdministrativa',
   GEEST_CODIGO  CHAR(10)      NOT NULL COMMENT 'CodigoEstanciaHospitalaria: Clave foránea hacia ESTANCIA_HOSPITALARIA (relación altaAdministrativa).',
   PEPER_CODIGO  CHAR(6)       NOT NULL COMMENT 'CodigoPersona: Clave foránea hacia PERSONA (relación responsableAlta).',
   FIREV_CODIGO  CHAR(10)      NOT NULL COMMENT 'CodigoRevisionCuenta: Clave foránea hacia REVISION_CUENTA (relación habilitaCierre).',
   CONSTRAINT PK_GEAAD PRIMARY KEY (GEAAD_CODIGO),
   CONSTRAINT AK_GEAAD_1 UNIQUE (GEEST_CODIGO),
   CONSTRAINT AK_GEAAD_2 UNIQUE (FIREV_CODIGO)
)
ENGINE = InnoDB
COMMENT = 'ALTA_ADMINISTRATIVA. Entidad utilizada para registrar el alta administrativa del paciente.';

CREATE INDEX IX_GEAAD_PEPER ON GEAAD_ALTAAD (PEPER_CODIGO);

/*==============================================================*/
/* Tabla: GETIP_TIPOES (TIPO_ESTUDIO_IMAGEN)                    */
/*==============================================================*/

CREATE TABLE GETIP_TIPOES
(
   GETIP_CODIGO  CHAR(4)       NOT NULL COMMENT 'CodigoTipoEstudioImagen',
   GETIP_CODEXT  CHAR(10)      NOT NULL COMMENT 'CodigoExternoTipoEstudioImagen',
   GETIP_NOMBRE  VARCHAR(255)  NOT NULL COMMENT 'NombreTipoEstudioImagen',
   GETIP_ACTIVO  BOOL          NOT NULL COMMENT 'ActivoTipoEstudioImagen',
   CONSTRAINT PK_GETIP PRIMARY KEY (GETIP_CODIGO)
)
ENGINE = InnoDB
COMMENT = 'TIPO_ESTUDIO_IMAGEN. Entidad utilizada para gestionar el catálogo de estudios de imagen.';

/*==============================================================*/
/* Tabla: GEREC_RECURS (RECURSO_IMAGEN)                         */
/*==============================================================*/

CREATE TABLE GEREC_RECURS
(
   GEREC_CODIGO  CHAR(4)       NOT NULL COMMENT 'CodigoRecursoImagen',
   GEREC_CODEXT  CHAR(10)      NOT NULL COMMENT 'CodigoExternoRecursoImagen',
   GEREC_NOMBRE  VARCHAR(255)  NOT NULL COMMENT 'NombreRecursoImagen',
   GEREC_ESTADO  VARCHAR(255)  NOT NULL COMMENT 'EstadoRecursoImagen',
   CONSTRAINT PK_GEREC PRIMARY KEY (GEREC_CODIGO)
)
ENGINE = InnoDB
COMMENT = 'RECURSO_IMAGEN. Entidad utilizada para gestionar los equipos y salas de diagnóstico por imágenes.';

/*==============================================================*/
/* Tabla: GEOIM_ORDENI (ORDEN_IMAGEN)                           */
/*==============================================================*/

CREATE TABLE GEOIM_ORDENI
(
   GEOIM_CODIGO  CHAR(10)      NOT NULL COMMENT 'CodigoOrdenImagen',
   GEOIM_SOLICI  DATETIME      NOT NULL COMMENT 'SolicitadaOrdenImagen',
   GEOIM_PRIORI  VARCHAR(255)  NOT NULL COMMENT 'PrioridadOrdenImagen',
   GEOIM_ESTADO  VARCHAR(255)  NOT NULL COMMENT 'EstadoOrdenImagen',
   GEOIM_MOTIVO  TEXT          NOT NULL COMMENT 'MotivoOrdenImagen',
   GEEPI_CODIGO  CHAR(10)      NOT NULL COMMENT 'CodigoEpisodioClinico: Clave foránea hacia EPISODIO_CLINICO (relación ordenesImagen).',
   PEPER_CODIGO  CHAR(6)       NOT NULL COMMENT 'CodigoPersona: Clave foránea hacia PERSONA (relación solicitanteImagen).',
   GETIP_CODIGO  CHAR(4)       NOT NULL COMMENT 'CodigoTipoEstudioImagen: Clave foránea hacia TIPO_ESTUDIO_IMAGEN (relación tipoEstudio).',
   CONSTRAINT PK_GEOIM PRIMARY KEY (GEOIM_CODIGO)
)
ENGINE = InnoDB
COMMENT = 'ORDEN_IMAGEN. Entidad utilizada para registrar las órdenes de estudios de imagen del paciente.';

CREATE INDEX IX_GEOIM_GEEPI ON GEOIM_ORDENI (GEEPI_CODIGO);

CREATE INDEX IX_GEOIM_PEPER ON GEOIM_ORDENI (PEPER_CODIGO);

CREATE INDEX IX_GEOIM_GETIP ON GEOIM_ORDENI (GETIP_CODIGO);

/*==============================================================*/
/* Tabla: GEPIM_PROGRA (PROGRAMACION_IMAGEN)                    */
/*==============================================================*/

CREATE TABLE GEPIM_PROGRA
(
   GEPIM_CODIGO  CHAR(10)      NOT NULL COMMENT 'CodigoProgramacionImagen',
   GEPIM_INICIO  DATETIME      NOT NULL COMMENT 'InicioProgramacionImagen',
   GEPIM_FIN     DATETIME      NOT NULL COMMENT 'FinProgramacionImagen',
   GEPIM_ESTADO  VARCHAR(255)  NOT NULL COMMENT 'EstadoProgramacionImagen',
   GEPIM_MOTCAM  VARCHAR(255)  NULL     COMMENT 'MotivoCambioProgramacionImagen',
   GEOIM_CODIGO  CHAR(10)      NOT NULL COMMENT 'CodigoOrdenImagen: Clave foránea hacia ORDEN_IMAGEN (relación programaciones).',
   GEREC_CODIGO  CHAR(4)       NOT NULL COMMENT 'CodigoRecursoImagen: Clave foránea hacia RECURSO_IMAGEN (relación reservasRecurso).',
   CONSTRAINT PK_GEPIM PRIMARY KEY (GEPIM_CODIGO)
)
ENGINE = InnoDB
COMMENT = 'PROGRAMACION_IMAGEN. Entidad utilizada para programar la realización de los estudios de imagen.';

CREATE INDEX IX_GEPIM_GEOIM ON GEPIM_PROGRA (GEOIM_CODIGO);

CREATE INDEX IX_GEPIM_GEREC ON GEPIM_PROGRA (GEREC_CODIGO);

/*==============================================================*/
/* Tabla: GEINF_INFORM (INFORME_IMAGEN)                         */
/*==============================================================*/

CREATE TABLE GEINF_INFORM
(
   GEINF_CODIGO  CHAR(10)      NOT NULL COMMENT 'CodigoInformeImagen',
   GEINF_ESTADO  VARCHAR(255)  NOT NULL COMMENT 'EstadoInformeImagen',
   GEINF_REALIZ  DATETIME      NOT NULL COMMENT 'RealizadoInformeImagen',
   GEINF_PUBLIC  DATETIME      NULL     COMMENT 'PublicadoInformeImagen',
   GEOIM_CODIGO  CHAR(10)      NOT NULL COMMENT 'CodigoOrdenImagen: Clave foránea hacia ORDEN_IMAGEN (relación informe).',
   CONSTRAINT PK_GEINF PRIMARY KEY (GEINF_CODIGO),
   CONSTRAINT AK_GEINF_1 UNIQUE (GEOIM_CODIGO)
)
ENGINE = InnoDB
COMMENT = 'INFORME_IMAGEN. Entidad utilizada para registrar los informes de los estudios de imagen.';

/*==============================================================*/
/* Tabla: GEVII_VERSIO (VERSION_INFORME_IMAGEN)                 */
/*==============================================================*/

CREATE TABLE GEVII_VERSIO
(
   GEVII_CODIGO  CHAR(12)  NOT NULL COMMENT 'CodigoVersionInformeImagen',
   GEVII_NUMERO  INT       NOT NULL COMMENT 'NumeroVersionInformeImagen',
   GEVII_CONTEN  TEXT      NOT NULL COMMENT 'ContenidoVersionInformeImagen',
   GEVII_INSTAN  DATETIME  NOT NULL COMMENT 'InstanteVersionInformeImagen',
   GEVII_MOTIVO  TEXT      NULL     COMMENT 'MotivoVersionInformeImagen',
   GEVII_VALIDA  DATETIME  NULL     COMMENT 'ValidadaVersionInformeImagen',
   GEINF_CODIGO  CHAR(10)  NOT NULL COMMENT 'CodigoInformeImagen: Clave foránea hacia INFORME_IMAGEN (relación versionesImagen).',
   PEPER_CODIGO  CHAR(6)   NOT NULL COMMENT 'CodigoPersona: Clave foránea hacia PERSONA (relación autorImagen).',
   CONSTRAINT PK_GEVII PRIMARY KEY (GEVII_CODIGO)
)
ENGINE = InnoDB
COMMENT = 'VERSION_INFORME_IMAGEN. Entidad utilizada para registrar las versiones de un informe de imagen.';

CREATE INDEX IX_GEVII_GEINF ON GEVII_VERSIO (GEINF_CODIGO);

CREATE INDEX IX_GEVII_PEPER ON GEVII_VERSIO (PEPER_CODIGO);

/*==============================================================*/
/* Tabla: GEADJ_ADJUNT (ADJUNTO_CLINICO)                        */
/*==============================================================*/

CREATE TABLE GEADJ_ADJUNT
(
   GEADJ_CODIGO  CHAR(12)      NOT NULL COMMENT 'CodigoAdjuntoClinico',
   GEADJ_NOMBRE  VARCHAR(255)  NOT NULL COMMENT 'NombreAdjuntoClinico',
   GEADJ_TIPO    VARCHAR(255)  NOT NULL COMMENT 'TipoAdjuntoClinico',
   GEADJ_BYTES   BIGINT        NOT NULL COMMENT 'BytesAdjuntoClinico',
   GEADJ_REFPRO  VARCHAR(255)  NOT NULL COMMENT 'ReferenciaProtegidaAdjuntoClinico',
   GEADJ_HASHIN  VARCHAR(128)  NOT NULL COMMENT 'HashIntegridadAdjuntoClinico',
   GEVII_CODIGO  CHAR(12)      NULL     COMMENT 'CodigoVersionInformeImagen: Clave foránea hacia VERSION_INFORME_IMAGEN (relación adjuntosImagen).',
   GEVIQ_CODIGO  CHAR(12)      NULL     COMMENT 'CodigoVersionInformeQuirurgico: Clave foránea hacia VERSION_INFORME_QUIRURGICO (relación adjuntosCirugia).',
   GENOT_CODIGO  CHAR(12)      NULL     COMMENT 'CodigoNotaClinica: Clave foránea hacia NOTA_CLINICA (relación adjuntosNota).',
   CONSTRAINT PK_GEADJ PRIMARY KEY (GEADJ_CODIGO)
)
ENGINE = InnoDB
COMMENT = 'ADJUNTO_CLINICO. Entidad utilizada para registrar los archivos adjuntos de notas e informes.';

CREATE INDEX IX_GEADJ_GEVII ON GEADJ_ADJUNT (GEVII_CODIGO);

CREATE INDEX IX_GEADJ_GEVIQ ON GEADJ_ADJUNT (GEVIQ_CODIGO);

CREATE INDEX IX_GEADJ_GENOT ON GEADJ_ADJUNT (GENOT_CODIGO);

/*==============================================================*/
/* Tabla: GETPR_TIPOPR (TIPO_PROCEDIMIENTO)                     */
/*==============================================================*/

CREATE TABLE GETPR_TIPOPR
(
   GETPR_CODIGO  CHAR(4)       NOT NULL COMMENT 'CodigoTipoProcedimiento',
   GETPR_CODEXT  CHAR(10)      NOT NULL COMMENT 'CodigoExternoTipoProcedimiento',
   GETPR_NOMBRE  VARCHAR(255)  NOT NULL COMMENT 'NombreTipoProcedimiento',
   GETPR_ACTIVO  BOOL          NOT NULL COMMENT 'ActivoTipoProcedimiento',
   CONSTRAINT PK_GETPR PRIMARY KEY (GETPR_CODIGO)
)
ENGINE = InnoDB
COMMENT = 'TIPO_PROCEDIMIENTO. Entidad utilizada para gestionar el catálogo de procedimientos quirúrgicos.';

/*==============================================================*/
/* Tabla: GESAL_SALAQU (SALA_QUIRURGICA)                        */
/*==============================================================*/

CREATE TABLE GESAL_SALAQU
(
   GESAL_CODIGO  CHAR(3)       NOT NULL COMMENT 'CodigoSalaQuirurgica',
   GESAL_CODEXT  CHAR(10)      NOT NULL COMMENT 'CodigoExternoSalaQuirurgica',
   GESAL_NOMBRE  VARCHAR(255)  NOT NULL COMMENT 'NombreSalaQuirurgica',
   GESAL_ESTADO  VARCHAR(255)  NOT NULL COMMENT 'EstadoSalaQuirurgica',
   CONSTRAINT PK_GESAL PRIMARY KEY (GESAL_CODIGO)
)
ENGINE = InnoDB
COMMENT = 'SALA_QUIRURGICA. Entidad utilizada para gestionar las salas de cirugía.';

/*==============================================================*/
/* Tabla: GESOL_SOLICI (SOLICITUD_QUIRURGICA)                   */
/*==============================================================*/

CREATE TABLE GESOL_SOLICI
(
   GESOL_CODIGO  CHAR(10)      NOT NULL COMMENT 'CodigoSolicitudQuirurgica',
   GESOL_SOLICI  DATETIME      NOT NULL COMMENT 'SolicitadaSolicitudQuirurgica',
   GESOL_INDICA  VARCHAR(255)  NOT NULL COMMENT 'IndicacionSolicitudQuirurgica',
   GESOL_ESTADO  VARCHAR(255)  NOT NULL COMMENT 'EstadoSolicitudQuirurgica',
   GEEPI_CODIGO  CHAR(10)      NOT NULL COMMENT 'CodigoEpisodioClinico: Clave foránea hacia EPISODIO_CLINICO (relación solicitudesCirugia).',
   PEPER_CODIGO  CHAR(6)       NOT NULL COMMENT 'CodigoPersona: Clave foránea hacia PERSONA (relación medicoSolicitante).',
   GETPR_CODIGO  CHAR(4)       NOT NULL COMMENT 'CodigoTipoProcedimiento: Clave foránea hacia TIPO_PROCEDIMIENTO (relación tipoProcedimiento).',
   CONSTRAINT PK_GESOL PRIMARY KEY (GESOL_CODIGO)
)
ENGINE = InnoDB
COMMENT = 'SOLICITUD_QUIRURGICA. Entidad utilizada para registrar las solicitudes de cirugía del paciente.';

CREATE INDEX IX_GESOL_GEEPI ON GESOL_SOLICI (GEEPI_CODIGO);

CREATE INDEX IX_GESOL_PEPER ON GESOL_SOLICI (PEPER_CODIGO);

CREATE INDEX IX_GESOL_GETPR ON GESOL_SOLICI (GETPR_CODIGO);

/*==============================================================*/
/* Tabla: GEPQU_PROGRA (PROGRAMACION_QUIRURGICA)                */
/*==============================================================*/

CREATE TABLE GEPQU_PROGRA
(
   GEPQU_CODIGO  CHAR(10)      NOT NULL COMMENT 'CodigoProgramacionQuirurgica',
   GEPQU_INICIO  DATETIME      NOT NULL COMMENT 'InicioProgramacionQuirurgica',
   GEPQU_FIN     DATETIME      NOT NULL COMMENT 'FinProgramacionQuirurgica',
   GEPQU_ESTADO  VARCHAR(255)  NOT NULL COMMENT 'EstadoProgramacionQuirurgica',
   GEPQU_MOTCAM  VARCHAR(255)  NULL     COMMENT 'MotivoCambioProgramacionQuirurgica',
   GESOL_CODIGO  CHAR(10)      NOT NULL COMMENT 'CodigoSolicitudQuirurgica: Clave foránea hacia SOLICITUD_QUIRURGICA (relación programacionesCirugia).',
   GESAL_CODIGO  CHAR(3)       NOT NULL COMMENT 'CodigoSalaQuirurgica: Clave foránea hacia SALA_QUIRURGICA (relación sala).',
   CONSTRAINT PK_GEPQU PRIMARY KEY (GEPQU_CODIGO)
)
ENGINE = InnoDB
COMMENT = 'PROGRAMACION_QUIRURGICA. Entidad utilizada para programar las cirugías.';

CREATE INDEX IX_GEPQU_GESOL ON GEPQU_PROGRA (GESOL_CODIGO);

CREATE INDEX IX_GEPQU_GESAL ON GEPQU_PROGRA (GESAL_CODIGO);

/*==============================================================*/
/* Tabla: GERQU_RESERV (RESERVA_QUIRURGICA)                     */
/*==============================================================*/

CREATE TABLE GERQU_RESERV
(
   GERQU_CODIGO  CHAR(10)      NOT NULL COMMENT 'CodigoReservaQuirurgica',
   GERQU_TIPREC  VARCHAR(255)  NOT NULL COMMENT 'TipoRecursoReservaQuirurgica',
   GERQU_REFREC  VARCHAR(255)  NOT NULL COMMENT 'ReferenciaRecursoReservaQuirurgica',
   GERQU_INICIO  DATETIME      NOT NULL COMMENT 'InicioReservaQuirurgica',
   GERQU_FIN     DATETIME      NOT NULL COMMENT 'FinReservaQuirurgica',
   GERQU_ESTADO  VARCHAR(255)  NOT NULL COMMENT 'EstadoReservaQuirurgica',
   GEPQU_CODIGO  CHAR(10)      NOT NULL COMMENT 'CodigoProgramacionQuirurgica: Clave foránea hacia PROGRAMACION_QUIRURGICA (relación reservasExclusivas).',
   CONSTRAINT PK_GERQU PRIMARY KEY (GERQU_CODIGO)
)
ENGINE = InnoDB
COMMENT = 'RESERVA_QUIRURGICA. Entidad utilizada para reservar los recursos de una cirugía.';

CREATE INDEX IX_GERQU_GEPQU ON GERQU_RESERV (GEPQU_CODIGO);

/*==============================================================*/
/* Tabla: GEPAQ_PARTIC (PARTICIPACION_QUIRURGICA)               */
/*==============================================================*/

CREATE TABLE GEPAQ_PARTIC
(
   GEPAQ_CODIGO  CHAR(12)      NOT NULL COMMENT 'CodigoParticipacionQuirurgica',
   GEPAQ_FUNCIO  VARCHAR(255)  NOT NULL COMMENT 'FuncionParticipacionQuirurgica',
   GEPAQ_HABILI  BOOL          NOT NULL COMMENT 'HabilitadaParticipacionQuirurgica',
   GEPQU_CODIGO  CHAR(10)      NOT NULL COMMENT 'CodigoProgramacionQuirurgica: Clave foránea hacia PROGRAMACION_QUIRURGICA (relación participantes).',
   PEPER_CODIGO  CHAR(6)       NOT NULL COMMENT 'CodigoPersona: Clave foránea hacia PERSONA (relación profesionalParticipante).',
   CONSTRAINT PK_GEPAQ PRIMARY KEY (GEPAQ_CODIGO)
)
ENGINE = InnoDB
COMMENT = 'PARTICIPACION_QUIRURGICA. Entidad utilizada para registrar los profesionales que participan en una cirugía.';

CREATE INDEX IX_GEPAQ_GEPQU ON GEPAQ_PARTIC (GEPQU_CODIGO);

CREATE INDEX IX_GEPAQ_PEPER ON GEPAQ_PARTIC (PEPER_CODIGO);

/*==============================================================*/
/* Tabla: GEIQU_INFORM (INFORME_QUIRURGICO)                     */
/*==============================================================*/

CREATE TABLE GEIQU_INFORM
(
   GEIQU_CODIGO  CHAR(10)      NOT NULL COMMENT 'CodigoInformeQuirurgico',
   GEIQU_TIPO    VARCHAR(255)  NOT NULL COMMENT 'TipoInformeQuirurgico',
   GEIQU_CONTEN  TEXT          NOT NULL COMMENT 'ContenidoInformeQuirurgico',
   GEIQU_ESTADO  VARCHAR(255)  NOT NULL COMMENT 'EstadoInformeQuirurgico',
   GEIQU_FINALI  DATETIME      NULL     COMMENT 'FinalizadoInformeQuirurgico',
   GESOL_CODIGO  CHAR(10)      NOT NULL COMMENT 'CodigoSolicitudQuirurgica: Clave foránea hacia SOLICITUD_QUIRURGICA (relación documentacion).',
   CONSTRAINT PK_GEIQU PRIMARY KEY (GEIQU_CODIGO)
)
ENGINE = InnoDB
COMMENT = 'INFORME_QUIRURGICO. Entidad utilizada para registrar los informes de las cirugías.';

CREATE INDEX IX_GEIQU_GESOL ON GEIQU_INFORM (GESOL_CODIGO);

/*==============================================================*/
/* Tabla: GEVIQ_VERSIO (VERSION_INFORME_QUIRURGICO)             */
/*==============================================================*/

CREATE TABLE GEVIQ_VERSIO
(
   GEVIQ_CODIGO  CHAR(12)  NOT NULL COMMENT 'CodigoVersionInformeQuirurgico',
   GEVIQ_NUMERO  INT       NOT NULL COMMENT 'NumeroVersionInformeQuirurgico',
   GEVIQ_CONTEN  TEXT      NOT NULL COMMENT 'ContenidoVersionInformeQuirurgico',
   GEVIQ_INSTAN  DATETIME  NOT NULL COMMENT 'InstanteVersionInformeQuirurgico',
   GEVIQ_MOTIVO  TEXT      NULL     COMMENT 'MotivoVersionInformeQuirurgico',
   GEVIQ_VALIDA  DATETIME  NULL     COMMENT 'ValidadaVersionInformeQuirurgico',
   GEIQU_CODIGO  CHAR(10)  NOT NULL COMMENT 'CodigoInformeQuirurgico: Clave foránea hacia INFORME_QUIRURGICO (relación versionesCirugia).',
   PEPER_CODIGO  CHAR(6)   NOT NULL COMMENT 'CodigoPersona: Clave foránea hacia PERSONA (relación autorCirugia).',
   CONSTRAINT PK_GEVIQ PRIMARY KEY (GEVIQ_CODIGO)
)
ENGINE = InnoDB
COMMENT = 'VERSION_INFORME_QUIRURGICO. Entidad utilizada para registrar las versiones de un informe quirúrgico.';

CREATE INDEX IX_GEVIQ_GEIQU ON GEVIQ_VERSIO (GEIQU_CODIGO);

CREATE INDEX IX_GEVIQ_PEPER ON GEVIQ_VERSIO (PEPER_CODIGO);

/*==============================================================*/
/* Tabla: GEDEF_DEFINI (DEFINICION_REPORTE)                     */
/*==============================================================*/

CREATE TABLE GEDEF_DEFINI
(
   GEDEF_CODIGO  CHAR(7)       NOT NULL COMMENT 'CodigoDefinicionReporte',
   GEDEF_CODEXT  CHAR(10)      NOT NULL COMMENT 'CodigoExternoDefinicionReporte',
   GEDEF_NOMBRE  VARCHAR(255)  NOT NULL COMMENT 'NombreDefinicionReporte',
   GEDEF_MODULO  VARCHAR(255)  NOT NULL COMMENT 'ModuloDefinicionReporte',
   GEDEF_FASE    INT           NOT NULL COMMENT 'FaseDefinicionReporte',
   GEDEF_ACTIVO  BOOL          NOT NULL COMMENT 'ActivoDefinicionReporte',
   SESIS_CODIGO  CHAR(2)       NOT NULL COMMENT 'CodigoSistema: Clave foránea hacia SISTEMA (relación reportesModulo).',
   CONSTRAINT PK_GEDEF PRIMARY KEY (GEDEF_CODIGO)
)
ENGINE = InnoDB
COMMENT = 'DEFINICION_REPORTE. Entidad utilizada para gestionar los reportes disponibles en el sistema.';

CREATE INDEX IX_GEDEF_SESIS ON GEDEF_DEFINI (SESIS_CODIGO);

/*==============================================================*/
/* Tabla: GECRE_CONSUL (CONSULTA_REPORTE)                       */
/*==============================================================*/

CREATE TABLE GECRE_CONSUL
(
   GECRE_CODIGO  CHAR(12)      NOT NULL COMMENT 'CodigoConsultaReporte',
   GECRE_INICIO  DATETIME      NOT NULL COMMENT 'InicioConsultaReporte',
   GECRE_FIN     DATETIME      NOT NULL COMMENT 'FinConsultaReporte',
   GECRE_CORTE   DATETIME      NOT NULL COMMENT 'CorteConsultaReporte',
   GECRE_AMBITO  VARCHAR(255)  NOT NULL COMMENT 'AmbitoConsultaReporte',
   GECRE_AGREGA  BOOL          NOT NULL COMMENT 'AgregadoConsultaReporte',
   GEDEF_CODIGO  CHAR(7)       NOT NULL COMMENT 'CodigoDefinicionReporte: Clave foránea hacia DEFINICION_REPORTE (relación consultas).',
   SEUSU_CODIGO  CHAR(6)       NOT NULL COMMENT 'CodigoUsuario: Clave foránea hacia USUARIO (relación consultaAutorizada).',
   CONSTRAINT PK_GECRE PRIMARY KEY (GECRE_CODIGO)
)
ENGINE = InnoDB
COMMENT = 'CONSULTA_REPORTE. Entidad utilizada para registrar las consultas de reportes realizadas por los usuarios.';

CREATE INDEX IX_GECRE_GEDEF ON GECRE_CONSUL (GEDEF_CODIGO);

CREATE INDEX IX_GECRE_SEUSU ON GECRE_CONSUL (SEUSU_CODIGO);

/*==============================================================*/
/* Tabla: GEEXP_EXPORT (EXPORTACION_REPORTE)                    */
/*==============================================================*/

CREATE TABLE GEEXP_EXPORT
(
   GEEXP_CODIGO  CHAR(12)      NOT NULL COMMENT 'CodigoExportacionReporte',
   GEEXP_INSTAN  DATETIME      NOT NULL COMMENT 'InstanteExportacionReporte',
   GEEXP_FORMAT  VARCHAR(255)  NOT NULL COMMENT 'FormatoExportacionReporte',
   GEEXP_AMBITO  VARCHAR(255)  NOT NULL COMMENT 'AmbitoExportacionReporte',
   GEEXP_ESTADO  VARCHAR(255)  NOT NULL COMMENT 'EstadoExportacionReporte',
   GECRE_CODIGO  CHAR(12)      NOT NULL COMMENT 'CodigoConsultaReporte: Clave foránea hacia CONSULTA_REPORTE (relación exportaciones).',
   CONSTRAINT PK_GEEXP PRIMARY KEY (GEEXP_CODIGO)
)
ENGINE = InnoDB
COMMENT = 'EXPORTACION_REPORTE. Entidad utilizada para registrar las exportaciones de reportes.';

CREATE INDEX IX_GEEXP_GECRE ON GEEXP_EXPORT (GECRE_CODIGO);

/*==============================================================*/
/* Tabla: GEPAR_PARAME (PARAMETRO_INSTITUCIONAL)                */
/*==============================================================*/

CREATE TABLE GEPAR_PARAME
(
   GEPAR_CODIGO  CHAR(6)       NOT NULL COMMENT 'CodigoParametroInstitucional',
   GEPAR_CODEXT  CHAR(20)      NOT NULL COMMENT 'CodigoExternoParametroInstitucional',
   GEPAR_VALOR   VARCHAR(255)  NOT NULL COMMENT 'ValorParametroInstitucional',
   GEPAR_VIGENT  BOOL          NOT NULL COMMENT 'VigenteParametroInstitucional',
   GEPAR_APROBA  VARCHAR(255)  NOT NULL COMMENT 'AprobacionParametroInstitucional',
   SESIS_CODIGO  CHAR(2)       NOT NULL COMMENT 'CodigoSistema: Clave foránea hacia SISTEMA (relación parametrosModulo).',
   CONSTRAINT PK_GEPAR PRIMARY KEY (GEPAR_CODIGO)
)
ENGINE = InnoDB
COMMENT = 'PARAMETRO_INSTITUCIONAL. Entidad utilizada para gestionar los parámetros generales del hospital.';

CREATE INDEX IX_GEPAR_SESIS ON GEPAR_PARAME (SESIS_CODIGO);

/*==============================================================*/
/* Tabla: GEDOL_DETALL (DETALLE_ORDEN_LABORATORIO)              */
/*==============================================================*/

CREATE TABLE GEDOL_DETALL
(
   GEDOL_CODIGO  CHAR(12)     NOT NULL COMMENT 'CodigoDetalleOrdenLaboratorio',
   GEDOL_INDICA  TEXT         NULL     COMMENT 'IndicacionesDetalleOrdenLaboratorio',
   GEDOL_ESTADO  VARCHAR(40)  NOT NULL COMMENT 'EstadoDetalleOrdenLaboratorio',
   GEEXA_CODIGO  CHAR(4)      NOT NULL COMMENT 'CodigoExamenLaboratorio: Clave foránea hacia EXAMEN_LABORATORIO (relación examenSolicitado).',
   GEORD_CODIGO  CHAR(10)     NOT NULL COMMENT 'CodigoOrdenLaboratorio: Clave foránea hacia ORDEN_LABORATORIO (relación examenesOrden).',
   CONSTRAINT PK_GEDOL PRIMARY KEY (GEDOL_CODIGO)
)
ENGINE = InnoDB
COMMENT = 'DETALLE_ORDEN_LABORATORIO. Entidad utilizada para registrar los exámenes solicitados en cada orden de laboratorio.';

CREATE INDEX IX_GEDOL_GEEXA ON GEDOL_DETALL (GEEXA_CODIGO);

CREATE INDEX IX_GEDOL_GEORD ON GEDOL_DETALL (GEORD_CODIGO);

/*==============================================================*/
/* Tabla: GEPCA_PREPAR (PREPARACION_CAMA)                       */
/*==============================================================*/

CREATE TABLE GEPCA_PREPAR
(
   GEPCA_CODIGO  CHAR(12)     NOT NULL COMMENT 'CodigoPreparacionCama',
   GEPCA_INSTAN  DATETIME     NOT NULL COMMENT 'InstantePreparacionCama',
   GEPCA_TIPO    VARCHAR(40)  NOT NULL COMMENT 'TipoPreparacionCama',
   GEPCA_CONFIR  BOOL         NOT NULL COMMENT 'ConfirmadaPreparacionCama',
   GEPCA_EVIDEN  TEXT         NOT NULL COMMENT 'EvidenciaPreparacionCama',
   GECAM_CODIGO  CHAR(5)      NOT NULL COMMENT 'CodigoCama: Clave foránea hacia CAMA (relación preparaciones).',
   PEPER_CODIGO  CHAR(6)      NOT NULL COMMENT 'CodigoPersona: Clave foránea hacia PERSONA (relación confirmaPreparacion).',
   CONSTRAINT PK_GEPCA PRIMARY KEY (GEPCA_CODIGO)
)
ENGINE = InnoDB
COMMENT = 'PREPARACION_CAMA. Entidad utilizada para registrar la limpieza y preparación de las camas.';

CREATE INDEX IX_GEPCA_GECAM ON GEPCA_PREPAR (GECAM_CODIGO);

CREATE INDEX IX_GEPCA_PEPER ON GEPCA_PREPAR (PEPER_CODIGO);

/*==============================================================*/
/* Tabla: GEACT_ACTUAC (ACTUACION_CLINICA)                      */
/*==============================================================*/

CREATE TABLE GEACT_ACTUAC
(
   GEACT_CODIGO  CHAR(12)     NOT NULL COMMENT 'CodigoActuacionClinica',
   GEACT_INSTAN  DATETIME     NOT NULL COMMENT 'InstanteActuacionClinica',
   GEACT_DESCRI  TEXT         NOT NULL COMMENT 'DescripcionActuacionClinica',
   GEACT_INDICA  TEXT         NULL     COMMENT 'IndicacionesActuacionClinica',
   GEACT_ESTADO  VARCHAR(40)  NOT NULL COMMENT 'EstadoActuacionClinica',
   GEEPI_CODIGO  CHAR(10)     NOT NULL COMMENT 'CodigoEpisodioClinico: Clave foránea hacia EPISODIO_CLINICO (relación actuaciones).',
   PEPER_CODIGO  CHAR(6)      NOT NULL COMMENT 'CodigoPersona: Clave foránea hacia PERSONA (relación autorActuacion).',
   CONSTRAINT PK_GEACT PRIMARY KEY (GEACT_CODIGO)
)
ENGINE = InnoDB
COMMENT = 'ACTUACION_CLINICA. Entidad utilizada para registrar los procedimientos no quirúrgicos realizados al paciente.';

CREATE INDEX IX_GEACT_GEEPI ON GEACT_ACTUAC (GEEPI_CODIGO);

CREATE INDEX IX_GEACT_PEPER ON GEACT_ACTUAC (PEPER_CODIGO);

/*==============================================================*/
/* Tabla: GEREP_RESPON (RESPONSABLE_EPISODIO)                   */
/*==============================================================*/

CREATE TABLE GEREP_RESPON
(
   GEEPI_CODIGO  CHAR(10)  NOT NULL COMMENT 'CodigoEpisodioClinico: Clave foránea hacia EPISODIO_CLINICO (relación responsablesEpisodio).',
   PEPER_CODIGO  CHAR(6)   NOT NULL COMMENT 'CodigoPersona: Clave foránea hacia PERSONA (relación responsableDe).',
   CONSTRAINT PK_GEREP PRIMARY KEY (GEEPI_CODIGO, PEPER_CODIGO)
)
ENGINE = InnoDB
COMMENT = 'RESPONSABLE_EPISODIO. Entidad asociativa que resuelve la relación N:M "responsablesEpisodio" entre EPISODIO_CLINICO y PERSONA (cada episodio clínico tiene 1..* responsables; cada persona puede ser responsable de 0..* episodios).';

CREATE INDEX IX_GEREP_PEPER ON GEREP_RESPON (PEPER_CODIGO);

/*==============================================================*/
/* Tabla: FICUE_CUENTA (CUENTA_HOSPITALARIA)                    */
/*==============================================================*/

CREATE TABLE FICUE_CUENTA
(
   FICUE_CODIGO  CHAR(10)       NOT NULL COMMENT 'CodigoCuentaHospitalaria',
   FICUE_ABIERT  DATETIME       NOT NULL COMMENT 'AbiertaCuentaHospitalaria',
   FICUE_ESTADO  VARCHAR(255)   NOT NULL COMMENT 'EstadoCuentaHospitalaria',
   FICUE_SALDO   DECIMAL(18,2)  NOT NULL COMMENT 'SaldoCuentaHospitalaria',
   FICUE_MONEDA  VARCHAR(255)   NOT NULL COMMENT 'MonedaCuentaHospitalaria',
   GEEPI_CODIGO  CHAR(10)       NOT NULL COMMENT 'CodigoEpisodioClinico: Clave foránea hacia EPISODIO_CLINICO (relación cuenta).',
   FIRES_CODIGO  CHAR(6)        NOT NULL COMMENT 'CodigoResponsablePago: Clave foránea hacia RESPONSABLE_PAGO (relación responsable).',
   CONSTRAINT PK_FICUE PRIMARY KEY (FICUE_CODIGO),
   CONSTRAINT AK_FICUE_1 UNIQUE (GEEPI_CODIGO)
)
ENGINE = InnoDB
COMMENT = 'CUENTA_HOSPITALARIA. Entidad utilizada para gestionar la cuenta de cada atención del paciente.';

CREATE INDEX IX_FICUE_FIRES ON FICUE_CUENTA (FIRES_CODIGO);

/*==============================================================*/
/* Tabla: FICAR_CARGOF (CARGO_FACTURABLE)                       */
/*==============================================================*/

CREATE TABLE FICAR_CARGOF
(
   FICAR_CODIGO  CHAR(12)       NOT NULL COMMENT 'CodigoCargoFacturable',
   FICAR_ORISER  VARCHAR(255)   NOT NULL COMMENT 'OrigenServicioCargoFacturable',
   FICAR_CLAORI  VARCHAR(255)   NOT NULL COMMENT 'ClaveOrigenCargoFacturable',
   FICAR_CANTID  DECIMAL(18,4)  NOT NULL COMMENT 'CantidadCargoFacturable',
   FICAR_IMPORT  DECIMAL(18,2)  NOT NULL COMMENT 'ImporteCargoFacturable',
   FICAR_ESTADO  VARCHAR(255)   NOT NULL COMMENT 'EstadoCargoFacturable',
   FICAR_PREUNI  DECIMAL(18,2)  NULL     COMMENT 'PrecioUnitarioCargoFacturable',
   FICAR_FECSER  DATETIME       NOT NULL COMMENT 'FechaServicioCargoFacturable',
   FICUE_CODIGO  CHAR(10)       NOT NULL COMMENT 'CodigoCuentaHospitalaria: Clave foránea hacia CUENTA_HOSPITALARIA (relación cargos).',
   FITAR_CODIGO  CHAR(6)        NULL     COMMENT 'CodigoTarifa: Clave foránea hacia TARIFA (relación tarifaAplicada).',
   CONSTRAINT PK_FICAR PRIMARY KEY (FICAR_CODIGO),
   CONSTRAINT AK_FICAR_1 UNIQUE (FICAR_ORISER, FICAR_CLAORI)
)
ENGINE = InnoDB
COMMENT = 'CARGO_FACTURABLE. Entidad utilizada para registrar los servicios que se cobran en la cuenta del paciente.';

CREATE INDEX IX_FICAR_FICUE ON FICAR_CARGOF (FICUE_CODIGO);

CREATE INDEX IX_FICAR_FITAR ON FICAR_CARGOF (FITAR_CODIGO);

/*==============================================================*/
/* Tabla: FITAR_TARIFA (TARIFA)                                 */
/*==============================================================*/

CREATE TABLE FITAR_TARIFA
(
   FITAR_CODIGO  CHAR(6)        NOT NULL COMMENT 'CodigoTarifa',
   FITAR_CONCEP  VARCHAR(255)   NOT NULL COMMENT 'ConceptoTarifa',
   FITAR_IMPORT  DECIMAL(18,2)  NOT NULL COMMENT 'ImporteTarifa',
   FITAR_DESDE   DATETIME       NOT NULL COMMENT 'DesdeTarifa',
   FITAR_HASTA   DATETIME       NULL     COMMENT 'HastaTarifa',
   FITAR_ACTIVO  BOOL           NOT NULL COMMENT 'ActivoTarifa',
   SEUSU_CODIGO  CHAR(6)        NOT NULL COMMENT 'CodigoUsuario: Clave foránea hacia USUARIO (relación autorizaTarifa).',
   CONSTRAINT PK_FITAR PRIMARY KEY (FITAR_CODIGO)
)
ENGINE = InnoDB
COMMENT = 'TARIFA. Entidad utilizada para gestionar los precios de los servicios del hospital.';

CREATE INDEX IX_FITAR_SEUSU ON FITAR_TARIFA (SEUSU_CODIGO);

/*==============================================================*/
/* Tabla: FIRES_RESPON (RESPONSABLE_PAGO)                       */
/*==============================================================*/

CREATE TABLE FIRES_RESPON
(
   FIRES_CODIGO  CHAR(6)       NOT NULL COMMENT 'CodigoResponsablePago',
   FIRES_TIPO    VARCHAR(255)  NOT NULL COMMENT 'TipoResponsablePago',
   FIRES_IDENTI  VARCHAR(255)  NOT NULL COMMENT 'IdentificacionResponsablePago',
   FIRES_NOMBRE  VARCHAR(255)  NOT NULL COMMENT 'NombreResponsablePago',
   FIRES_REFCOB  VARCHAR(255)  NULL     COMMENT 'ReferenciaCoberturaResponsablePago',
   CONSTRAINT PK_FIRES PRIMARY KEY (FIRES_CODIGO)
)
ENGINE = InnoDB
COMMENT = 'RESPONSABLE_PAGO. Entidad utilizada para registrar quién paga la cuenta (paciente, seguro u otra entidad).';

/*==============================================================*/
/* Tabla: FIPAG_PAGO (PAGO)                                     */
/*==============================================================*/

CREATE TABLE FIPAG_PAGO
(
   FIPAG_CODIGO  CHAR(10)       NOT NULL COMMENT 'CodigoPago',
   FIPAG_IMPORT  DECIMAL(18,2)  NOT NULL COMMENT 'ImportePago',
   FIPAG_INSTAN  DATETIME       NOT NULL COMMENT 'InstantePago',
   FIPAG_MEDIO   VARCHAR(255)   NOT NULL COMMENT 'MedioPago',
   FIPAG_ESTADO  VARCHAR(255)   NOT NULL COMMENT 'EstadoPago',
   FIPAG_CLAOPE  VARCHAR(255)   NOT NULL COMMENT 'ClaveOperacionPago',
   FICUE_CODIGO  CHAR(10)       NOT NULL COMMENT 'CodigoCuentaHospitalaria: Clave foránea hacia CUENTA_HOSPITALARIA (relación pagos).',
   SEUSU_CODIGO  CHAR(6)        NOT NULL COMMENT 'CodigoUsuario: Clave foránea hacia USUARIO (relación registraPago).',
   CONSTRAINT PK_FIPAG PRIMARY KEY (FIPAG_CODIGO),
   CONSTRAINT AK_FIPAG_1 UNIQUE (FIPAG_CLAOPE)
)
ENGINE = InnoDB
COMMENT = 'PAGO. Entidad utilizada para registrar los pagos realizados a una cuenta.';

CREATE INDEX IX_FIPAG_FICUE ON FIPAG_PAGO (FICUE_CODIGO);

CREATE INDEX IX_FIPAG_SEUSU ON FIPAG_PAGO (SEUSU_CODIGO);

/*==============================================================*/
/* Tabla: FICOM_COMPRO (COMPROBANTE)                            */
/*==============================================================*/

CREATE TABLE FICOM_COMPRO
(
   FICOM_CODIGO  CHAR(17)      NOT NULL COMMENT 'CodigoComprobante',
   FICOM_NUMERO  VARCHAR(255)  NOT NULL COMMENT 'NumeroComprobante',
   FICOM_EMITID  DATETIME      NOT NULL COMMENT 'EmitidoComprobante',
   FICOM_ESTADO  VARCHAR(255)  NOT NULL COMMENT 'EstadoComprobante',
   FIPAG_CODIGO  CHAR(10)      NOT NULL COMMENT 'CodigoPago: Clave foránea hacia PAGO (relación comprobante).',
   CONSTRAINT PK_FICOM PRIMARY KEY (FICOM_CODIGO),
   CONSTRAINT AK_FICOM_1 UNIQUE (FICOM_NUMERO),
   CONSTRAINT AK_FICOM_2 UNIQUE (FIPAG_CODIGO)
)
ENGINE = InnoDB
COMMENT = 'COMPROBANTE. Entidad utilizada para registrar los comprobantes emitidos por cada pago.';

/*==============================================================*/
/* Tabla: FIDES_DESCUE (DESCUENTO)                              */
/*==============================================================*/

CREATE TABLE FIDES_DESCUE
(
   FIDES_CODIGO  CHAR(8)        NOT NULL COMMENT 'CodigoDescuento',
   FIDES_IMPORT  DECIMAL(18,2)  NOT NULL COMMENT 'ImporteDescuento',
   FIDES_MOTIVO  TEXT           NOT NULL COMMENT 'MotivoDescuento',
   FIDES_AUTORI  DATETIME       NULL     COMMENT 'AutorizadoDescuento',
   FICUE_CODIGO  CHAR(10)       NOT NULL COMMENT 'CodigoCuentaHospitalaria: Clave foránea hacia CUENTA_HOSPITALARIA (relación descuentos).',
   PEPER_CODIGO  CHAR(6)        NOT NULL COMMENT 'CodigoPersona: Clave foránea hacia PERSONA (relación autorizaDescuento).',
   CONSTRAINT PK_FIDES PRIMARY KEY (FIDES_CODIGO)
)
ENGINE = InnoDB
COMMENT = 'DESCUENTO. Entidad utilizada para registrar los descuentos aplicados a una cuenta.';

CREATE INDEX IX_FIDES_FICUE ON FIDES_DESCUE (FICUE_CODIGO);

CREATE INDEX IX_FIDES_PEPER ON FIDES_DESCUE (PEPER_CODIGO);

/*==============================================================*/
/* Tabla: FICMP_COMPEN (COMPENSACION)                           */
/*==============================================================*/

CREATE TABLE FICMP_COMPEN
(
   FICMP_CODIGO  CHAR(8)        NOT NULL COMMENT 'CodigoCompensacion',
   FICMP_TIPO    VARCHAR(255)   NOT NULL COMMENT 'TipoCompensacion',
   FICMP_IMPORT  DECIMAL(18,2)  NOT NULL COMMENT 'ImporteCompensacion',
   FICMP_INSTAN  DATETIME       NOT NULL COMMENT 'InstanteCompensacion',
   FICMP_MOTIVO  TEXT           NOT NULL COMMENT 'MotivoCompensacion',
   FICUE_CODIGO  CHAR(10)       NOT NULL COMMENT 'CodigoCuentaHospitalaria: Clave foránea hacia CUENTA_HOSPITALARIA (relación compensaciones).',
   FICAR_CODIGO  CHAR(12)       NULL     COMMENT 'CodigoCargoFacturable: Clave foránea hacia CARGO_FACTURABLE (relación compensaCargo).',
   FIPAG_CODIGO  CHAR(10)       NULL     COMMENT 'CodigoPago: Clave foránea hacia PAGO (relación compensaPago).',
   SEUSU_CODIGO  CHAR(6)        NOT NULL COMMENT 'CodigoUsuario: Clave foránea hacia USUARIO (relación registraCompensacion).',
   CONSTRAINT PK_FICMP PRIMARY KEY (FICMP_CODIGO)
)
ENGINE = InnoDB
COMMENT = 'COMPENSACION. Entidad utilizada para registrar las anulaciones o correcciones de cargos y pagos.';

CREATE INDEX IX_FICMP_FICUE ON FICMP_COMPEN (FICUE_CODIGO);

CREATE INDEX IX_FICMP_FICAR ON FICMP_COMPEN (FICAR_CODIGO);

CREATE INDEX IX_FICMP_FIPAG ON FICMP_COMPEN (FIPAG_CODIGO);

CREATE INDEX IX_FICMP_SEUSU ON FICMP_COMPEN (SEUSU_CODIGO);

/*==============================================================*/
/* Tabla: FIREV_REVISI (REVISION_CUENTA)                        */
/*==============================================================*/

CREATE TABLE FIREV_REVISI
(
   FIREV_CODIGO  CHAR(10)      NOT NULL COMMENT 'CodigoRevisionCuenta',
   FIREV_INSTAN  DATETIME      NOT NULL COMMENT 'InstanteRevisionCuenta',
   FIREV_ESTADO  VARCHAR(255)  NOT NULL COMMENT 'EstadoRevisionCuenta',
   FIREV_OBSERV  TEXT          NULL     COMMENT 'ObservacionRevisionCuenta',
   FICUE_CODIGO  CHAR(10)      NOT NULL COMMENT 'CodigoCuentaHospitalaria: Clave foránea hacia CUENTA_HOSPITALARIA (relación revisiones).',
   PEPER_CODIGO  CHAR(6)       NOT NULL COMMENT 'CodigoPersona: Clave foránea hacia PERSONA (relación revisor).',
   CONSTRAINT PK_FIREV PRIMARY KEY (FIREV_CODIGO)
)
ENGINE = InnoDB
COMMENT = 'REVISION_CUENTA. Entidad utilizada para registrar la revisión de la cuenta antes del alta administrativa.';

CREATE INDEX IX_FIREV_FICUE ON FIREV_REVISI (FICUE_CODIGO);

CREATE INDEX IX_FIREV_PEPER ON FIREV_REVISI (PEPER_CODIGO);

/*==============================================================*/
/* Claves foráneas                                              */
/*==============================================================*/

ALTER TABLE SEUSU_USUARI ADD CONSTRAINT FK_SEUSU_PEPER_CUENTA FOREIGN KEY (PEPER_CODIGO)
      REFERENCES PEPER_PERSON (PEPER_CODIGO) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE SESES_SESION ADD CONSTRAINT FK_SESES_SEUSU_SESIONES FOREIGN KEY (SEUSU_CODIGO)
      REFERENCES SEUSU_USUARI (SEUSU_CODIGO) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE SECRE_CREDEN ADD CONSTRAINT FK_SECRE_SEUSU_CREDENCIALES FOREIGN KEY (SEUSU_CODIGO)
      REFERENCES SEUSU_USUARI (SEUSU_CODIGO) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE SEUXP_USUPER ADD CONSTRAINT FK_SEUXP_SEUSU_ROLESASIGNADOS FOREIGN KEY (SEUSU_CODIGO)
      REFERENCES SEUSU_USUARI (SEUSU_CODIGO) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE SEUXP_USUPER ADD CONSTRAINT FK_SEUXP_SEPER_ASIGNACIONES FOREIGN KEY (SEPER_CODIGO)
      REFERENCES SEPER_PERFIL (SEPER_CODIGO) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE SEOXP_OPCPER ADD CONSTRAINT FK_SEOXP_SEPER_PERMISOS FOREIGN KEY (SEPER_CODIGO)
      REFERENCES SEPER_PERFIL (SEPER_CODIGO) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE SEOXP_OPCPER ADD CONSTRAINT FK_SEOXP_SEOPC_CONCESIONES FOREIGN KEY (SEOPC_CODIGO)
      REFERENCES SEOPC_OPCION (SEOPC_CODIGO) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE GECID_CONCIL ADD CONSTRAINT FK_GECID_GEPAC_DESTINOCONCILIADO FOREIGN KEY (GEPAC_CODIGO_DESTIN)
      REFERENCES GEPAC_PACIEN (GEPAC_CODIGO) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE GECID_CONCIL ADD CONSTRAINT FK_GECID_GEPAC_ORIGENPROVISIONAL FOREIGN KEY (GEPAC_CODIGO_ORIGEN)
      REFERENCES GEPAC_PACIEN (GEPAC_CODIGO) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE GECID_CONCIL ADD CONSTRAINT FK_GECID_SEUSU_AUTORIZACONCILIACION FOREIGN KEY (SEUSU_CODIGO)
      REFERENCES SEUSU_USUARI (SEUSU_CODIGO) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE GEDIS_DISPON ADD CONSTRAINT FK_GEDIS_PEESP_HORARIOS FOREIGN KEY (PEESP_CODIGO)
      REFERENCES PEESP_ESPECI (PEESP_CODIGO) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE GEDIS_DISPON ADD CONSTRAINT FK_GEDIS_PEPER_PROFESIONAL FOREIGN KEY (PEPER_CODIGO)
      REFERENCES PEPER_PERSON (PEPER_CODIGO) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE GEDIS_DISPON ADD CONSTRAINT FK_GEDIS_GECON_DISPONIBILIDAD FOREIGN KEY (GECON_CODIGO)
      REFERENCES GECON_CONSUL (GECON_CODIGO) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE GECIT_CITA ADD CONSTRAINT FK_GECIT_GEPAC_CITAS FOREIGN KEY (GEPAC_CODIGO)
      REFERENCES GEPAC_PACIEN (GEPAC_CODIGO) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE GECIT_CITA ADD CONSTRAINT FK_GECIT_GEDIS_RESERVAS FOREIGN KEY (GEDIS_CODIGO)
      REFERENCES GEDIS_DISPON (GEDIS_CODIGO) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE GECIT_CITA ADD CONSTRAINT FK_GECIT_PEPER_MEDICOAGENDA FOREIGN KEY (PEPER_CODIGO)
      REFERENCES PEPER_PERSON (PEPER_CODIGO) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE GECIT_CITA ADD CONSTRAINT FK_GECIT_GECON_CONSULTORIOCITA FOREIGN KEY (GECON_CODIGO)
      REFERENCES GECON_CONSUL (GECON_CODIGO) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE GEHIS_HISTOR ADD CONSTRAINT FK_GEHIS_GEPAC_HISTORIA FOREIGN KEY (GEPAC_CODIGO)
      REFERENCES GEPAC_PACIEN (GEPAC_CODIGO) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE GEEPI_EPISOD ADD CONSTRAINT FK_GEEPI_GEPAC_EPISODIOS FOREIGN KEY (GEPAC_CODIGO)
      REFERENCES GEPAC_PACIEN (GEPAC_CODIGO) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE GEEPI_EPISOD ADD CONSTRAINT FK_GEEPI_GEHIS_CRONOLOGIA FOREIGN KEY (GEHIS_CODIGO)
      REFERENCES GEHIS_HISTOR (GEHIS_CODIGO) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE GEEPI_EPISOD ADD CONSTRAINT FK_GEEPI_GECIT_ORIGINACONSULTA FOREIGN KEY (GECIT_CODIGO)
      REFERENCES GECIT_CITA (GECIT_CODIGO) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE GENOT_NOTACL ADD CONSTRAINT FK_GENOT_GEEPI_NOTAS FOREIGN KEY (GEEPI_CODIGO)
      REFERENCES GEEPI_EPISOD (GEEPI_CODIGO) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE GENOT_NOTACL ADD CONSTRAINT FK_GENOT_PEPER_AUTORNOTA FOREIGN KEY (PEPER_CODIGO)
      REFERENCES PEPER_PERSON (PEPER_CODIGO) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE GEADE_ADENDA ADD CONSTRAINT FK_GEADE_GENOT_ADENDAS FOREIGN KEY (GENOT_CODIGO)
      REFERENCES GENOT_NOTACL (GENOT_CODIGO) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE GEADE_ADENDA ADD CONSTRAINT FK_GEADE_PEPER_AUTORADENDA FOREIGN KEY (PEPER_CODIGO)
      REFERENCES PEPER_PERSON (PEPER_CODIGO) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE GESIG_SIGNOV ADD CONSTRAINT FK_GESIG_GEEPI_SIGNOS FOREIGN KEY (GEEPI_CODIGO)
      REFERENCES GEEPI_EPISOD (GEEPI_CODIGO) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE GESIG_SIGNOV ADD CONSTRAINT FK_GESIG_PEPER_REGISTRASIGNO FOREIGN KEY (PEPER_CODIGO)
      REFERENCES PEPER_PERSON (PEPER_CODIGO) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE GEANT_ANTECE ADD CONSTRAINT FK_GEANT_GEHIS_ANTECEDENTES FOREIGN KEY (GEHIS_CODIGO)
      REFERENCES GEHIS_HISTOR (GEHIS_CODIGO) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE GEALE_ALERGI ADD CONSTRAINT FK_GEALE_GEHIS_ALERGIAS FOREIGN KEY (GEHIS_CODIGO)
      REFERENCES GEHIS_HISTOR (GEHIS_CODIGO) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE GEANT_ANTECE ADD CONSTRAINT FK_GEANT_GEANT_RECTIFICACIONES FOREIGN KEY (GEANT_CODIGO_RECTIF)
      REFERENCES GEANT_ANTECE (GEANT_CODIGO) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE GEALE_ALERGI ADD CONSTRAINT FK_GEALE_GEALE_RECTIFICACIONES FOREIGN KEY (GEALE_CODIGO_RECTIF)
      REFERENCES GEALE_ALERGI (GEALE_CODIGO) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE GEANT_ANTECE ADD CONSTRAINT FK_GEANT_PEPER_AUTORANTECEDENTE FOREIGN KEY (PEPER_CODIGO)
      REFERENCES PEPER_PERSON (PEPER_CODIGO) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE GEALE_ALERGI ADD CONSTRAINT FK_GEALE_PEPER_AUTORALERGIA FOREIGN KEY (PEPER_CODIGO)
      REFERENCES PEPER_PERSON (PEPER_CODIGO) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE GEDIA_DIAGNO ADD CONSTRAINT FK_GEDIA_GEEPI_DIAGNOSTICOS FOREIGN KEY (GEEPI_CODIGO)
      REFERENCES GEEPI_EPISOD (GEEPI_CODIGO) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE GEDIA_DIAGNO ADD CONSTRAINT FK_GEDIA_PEPER_MEDICODIAGNOSTICO FOREIGN KEY (PEPER_CODIGO)
      REFERENCES PEPER_PERSON (PEPER_CODIGO) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE GEPRE_PRESCR ADD CONSTRAINT FK_GEPRE_GEEPI_PRESCRIPCIONES FOREIGN KEY (GEEPI_CODIGO)
      REFERENCES GEEPI_EPISOD (GEEPI_CODIGO) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE GEPRE_PRESCR ADD CONSTRAINT FK_GEPRE_PEPER_PRESCRIPTOR FOREIGN KEY (PEPER_CODIGO)
      REFERENCES PEPER_PERSON (PEPER_CODIGO) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE GEDET_DETALL ADD CONSTRAINT FK_GEDET_GEPRE_ITEMS FOREIGN KEY (GEPRE_CODIGO)
      REFERENCES GEPRE_PRESCR (GEPRE_CODIGO) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE GEDET_DETALL ADD CONSTRAINT FK_GEDET_GEMED_MEDICAMENTOPRESCRITO FOREIGN KEY (GEMED_CODIGO)
      REFERENCES GEMED_MEDICA (GEMED_CODIGO) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE GECUI_CUIDAD ADD CONSTRAINT FK_GECUI_GEEPI_CUIDADOS FOREIGN KEY (GEEPI_CODIGO)
      REFERENCES GEEPI_EPISOD (GEEPI_CODIGO) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE GECUI_CUIDAD ADD CONSTRAINT FK_GECUI_GENOT_DOCUMENTACUIDADO FOREIGN KEY (GENOT_CODIGO)
      REFERENCES GENOT_NOTACL (GENOT_CODIGO) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE GECUI_CUIDAD ADD CONSTRAINT FK_GECUI_PEPER_ENFERMERO FOREIGN KEY (PEPER_CODIGO)
      REFERENCES PEPER_PERSON (PEPER_CODIGO) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE GETRI_TRIAJE ADD CONSTRAINT FK_GETRI_GEEPI_CLASIFICACIONES FOREIGN KEY (GEEPI_CODIGO)
      REFERENCES GEEPI_EPISOD (GEEPI_CODIGO) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE GETRI_TRIAJE ADD CONSTRAINT FK_GETRI_PEPER_CLASIFICADOR FOREIGN KEY (PEPER_CODIGO)
      REFERENCES PEPER_PERSON (PEPER_CODIGO) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE GEORD_ORDENL ADD CONSTRAINT FK_GEORD_GEEPI_ORDENESLABORATORIO FOREIGN KEY (GEEPI_CODIGO)
      REFERENCES GEEPI_EPISOD (GEEPI_CODIGO) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE GEORD_ORDENL ADD CONSTRAINT FK_GEORD_PEPER_SOLICITANTELABORATORIO FOREIGN KEY (PEPER_CODIGO)
      REFERENCES PEPER_PERSON (PEPER_CODIGO) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE GEDOL_DETALL ADD CONSTRAINT FK_GEDOL_GEEXA_EXAMENSOLICITADO FOREIGN KEY (GEEXA_CODIGO)
      REFERENCES GEEXA_EXAMEN (GEEXA_CODIGO) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE GEMUE_MUESTR ADD CONSTRAINT FK_GEMUE_GEORD_MUESTRAS FOREIGN KEY (GEORD_CODIGO)
      REFERENCES GEORD_ORDENL (GEORD_CODIGO) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE GERES_RESULT ADD CONSTRAINT FK_GERES_GEDOL_RESULTADO FOREIGN KEY (GEDOL_CODIGO)
      REFERENCES GEDOL_DETALL (GEDOL_CODIGO) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE GERES_RESULT ADD CONSTRAINT FK_GERES_GEMUE_PROCESAMIENTO FOREIGN KEY (GEMUE_CODIGO)
      REFERENCES GEMUE_MUESTR (GEMUE_CODIGO) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE GEVER_VERSIO ADD CONSTRAINT FK_GEVER_GERES_VERSIONES FOREIGN KEY (GERES_CODIGO)
      REFERENCES GERES_RESULT (GERES_CODIGO) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE GEVER_VERSIO ADD CONSTRAINT FK_GEVER_PEPER_VALIDADORRESULTADO FOREIGN KEY (PEPER_CODIGO)
      REFERENCES PEPER_PERSON (PEPER_CODIGO) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE GECOM_COMUNI ADD CONSTRAINT FK_GECOM_GERES_COMUNICACIONES FOREIGN KEY (GERES_CODIGO)
      REFERENCES GERES_RESULT (GERES_CODIGO) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE GECOM_COMUNI ADD CONSTRAINT FK_GECOM_PEPER_COMUNICACRITICO FOREIGN KEY (PEPER_CODIGO)
      REFERENCES PEPER_PERSON (PEPER_CODIGO) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE GELOT_LOTEME ADD CONSTRAINT FK_GELOT_GEMED_LOTES FOREIGN KEY (GEMED_CODIGO)
      REFERENCES GEMED_MEDICA (GEMED_CODIGO) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE GELOT_LOTEME ADD CONSTRAINT FK_GELOT_GEPRO_SUMINISTRO FOREIGN KEY (GEPRO_CODIGO)
      REFERENCES GEPRO_PROVEE (GEPRO_CODIGO) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE GEMOV_MOVIMI ADD CONSTRAINT FK_GEMOV_GELOT_MOVIMIENTOS FOREIGN KEY (GELOT_CODIGO)
      REFERENCES GELOT_LOTEME (GELOT_CODIGO) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE GEMOV_MOVIMI ADD CONSTRAINT FK_GEMOV_PEPER_RESPONSABLEMOVIMIENTO FOREIGN KEY (PEPER_CODIGO)
      REFERENCES PEPER_PERSON (PEPER_CODIGO) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE GEDSP_DISPEN ADD CONSTRAINT FK_GEDSP_GEPRE_ENTREGAS FOREIGN KEY (GEPRE_CODIGO)
      REFERENCES GEPRE_PRESCR (GEPRE_CODIGO) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE GEDSP_DISPEN ADD CONSTRAINT FK_GEDSP_PEPER_FARMACEUTICO FOREIGN KEY (PEPER_CODIGO)
      REFERENCES PEPER_PERSON (PEPER_CODIGO) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE GEDDI_DETALL ADD CONSTRAINT FK_GEDDI_GEDSP_DETALLES FOREIGN KEY (GEDSP_CODIGO)
      REFERENCES GEDSP_DISPEN (GEDSP_CODIGO) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE GEDDI_DETALL ADD CONSTRAINT FK_GEDDI_GEDET_ITEMENTREGADO FOREIGN KEY (GEDET_CODIGO)
      REFERENCES GEDET_DETALL (GEDET_CODIGO) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE GEDDI_DETALL ADD CONSTRAINT FK_GEDDI_GELOT_LOTEENTREGADO FOREIGN KEY (GELOT_CODIGO)
      REFERENCES GELOT_LOTEME (GELOT_CODIGO) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE GEDDI_DETALL ADD CONSTRAINT FK_GEDDI_GEMOV_SALIDASTOCK FOREIGN KEY (GEMOV_CODIGO)
      REFERENCES GEMOV_MOVIMI (GEMOV_CODIGO) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE GEDEV_DEVOLU ADD CONSTRAINT FK_GEDEV_GEDDI_DEVOLUCIONES FOREIGN KEY (GEDDI_CODIGO)
      REFERENCES GEDDI_DETALL (GEDDI_CODIGO) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE GEMOV_MOVIMI ADD CONSTRAINT FK_GEMOV_GEDEV_ENTRADAAPROBADA FOREIGN KEY (GEDEV_CODIGO)
      REFERENCES GEDEV_DEVOLU (GEDEV_CODIGO) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE GECAM_CAMA ADD CONSTRAINT FK_GECAM_GEUNI_CAMAS FOREIGN KEY (GEUNI_CODIGO)
      REFERENCES GEUNI_UNIDAD (GEUNI_CODIGO) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE GEOIN_ORDENI ADD CONSTRAINT FK_GEOIN_GEEPI_ORDENESINGRESO FOREIGN KEY (GEEPI_CODIGO)
      REFERENCES GEEPI_EPISOD (GEEPI_CODIGO) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE GEOIN_ORDENI ADD CONSTRAINT FK_GEOIN_PEPER_MEDICOINGRESO FOREIGN KEY (PEPER_CODIGO)
      REFERENCES PEPER_PERSON (PEPER_CODIGO) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE GEEST_ESTANC ADD CONSTRAINT FK_GEEST_GEOIN_ADMISION FOREIGN KEY (GEOIN_CODIGO)
      REFERENCES GEOIN_ORDENI (GEOIN_CODIGO) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE GEMCA_MOVIMI ADD CONSTRAINT FK_GEMCA_GEEST_MOVIMIENTOS FOREIGN KEY (GEEST_CODIGO)
      REFERENCES GEEST_ESTANC (GEEST_CODIGO) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE GEMCA_MOVIMI ADD CONSTRAINT FK_GEMCA_GECAM_OCUPACIONES FOREIGN KEY (GECAM_CODIGO)
      REFERENCES GECAM_CAMA (GECAM_CODIGO) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE GERCA_RESERV ADD CONSTRAINT FK_GERCA_GECAM_RESERVASCAMA FOREIGN KEY (GECAM_CODIGO)
      REFERENCES GECAM_CAMA (GECAM_CODIGO) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE GERCA_RESERV ADD CONSTRAINT FK_GERCA_GEPAC_RESERVASPACIENTE FOREIGN KEY (GEPAC_CODIGO)
      REFERENCES GEPAC_PACIEN (GEPAC_CODIGO) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE GEALT_ALTAME ADD CONSTRAINT FK_GEALT_GEEST_ALTAMEDICA FOREIGN KEY (GEEST_CODIGO)
      REFERENCES GEEST_ESTANC (GEEST_CODIGO) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE GEALT_ALTAME ADD CONSTRAINT FK_GEALT_PEPER_MEDICOALTA FOREIGN KEY (PEPER_CODIGO)
      REFERENCES PEPER_PERSON (PEPER_CODIGO) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE GEAAD_ALTAAD ADD CONSTRAINT FK_GEAAD_GEEST_ALTAADMINISTRATIVA FOREIGN KEY (GEEST_CODIGO)
      REFERENCES GEEST_ESTANC (GEEST_CODIGO) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE GEAAD_ALTAAD ADD CONSTRAINT FK_GEAAD_PEPER_RESPONSABLEALTA FOREIGN KEY (PEPER_CODIGO)
      REFERENCES PEPER_PERSON (PEPER_CODIGO) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE GEAAD_ALTAAD ADD CONSTRAINT FK_GEAAD_FIREV_HABILITACIERRE FOREIGN KEY (FIREV_CODIGO)
      REFERENCES FIREV_REVISI (FIREV_CODIGO) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE FICUE_CUENTA ADD CONSTRAINT FK_FICUE_GEEPI_CUENTA FOREIGN KEY (GEEPI_CODIGO)
      REFERENCES GEEPI_EPISOD (GEEPI_CODIGO) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE FICAR_CARGOF ADD CONSTRAINT FK_FICAR_FICUE_CARGOS FOREIGN KEY (FICUE_CODIGO)
      REFERENCES FICUE_CUENTA (FICUE_CODIGO) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE FICAR_CARGOF ADD CONSTRAINT FK_FICAR_FITAR_TARIFAAPLICADA FOREIGN KEY (FITAR_CODIGO)
      REFERENCES FITAR_TARIFA (FITAR_CODIGO) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE FICUE_CUENTA ADD CONSTRAINT FK_FICUE_FIRES_RESPONSABLE FOREIGN KEY (FIRES_CODIGO)
      REFERENCES FIRES_RESPON (FIRES_CODIGO) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE FIPAG_PAGO ADD CONSTRAINT FK_FIPAG_FICUE_PAGOS FOREIGN KEY (FICUE_CODIGO)
      REFERENCES FICUE_CUENTA (FICUE_CODIGO) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE FICOM_COMPRO ADD CONSTRAINT FK_FICOM_FIPAG_COMPROBANTE FOREIGN KEY (FIPAG_CODIGO)
      REFERENCES FIPAG_PAGO (FIPAG_CODIGO) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE FIDES_DESCUE ADD CONSTRAINT FK_FIDES_FICUE_DESCUENTOS FOREIGN KEY (FICUE_CODIGO)
      REFERENCES FICUE_CUENTA (FICUE_CODIGO) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE FIDES_DESCUE ADD CONSTRAINT FK_FIDES_PEPER_AUTORIZADESCUENTO FOREIGN KEY (PEPER_CODIGO)
      REFERENCES PEPER_PERSON (PEPER_CODIGO) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE FICMP_COMPEN ADD CONSTRAINT FK_FICMP_FICUE_COMPENSACIONES FOREIGN KEY (FICUE_CODIGO)
      REFERENCES FICUE_CUENTA (FICUE_CODIGO) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE FICMP_COMPEN ADD CONSTRAINT FK_FICMP_FICAR_COMPENSACARGO FOREIGN KEY (FICAR_CODIGO)
      REFERENCES FICAR_CARGOF (FICAR_CODIGO) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE FICMP_COMPEN ADD CONSTRAINT FK_FICMP_FIPAG_COMPENSAPAGO FOREIGN KEY (FIPAG_CODIGO)
      REFERENCES FIPAG_PAGO (FIPAG_CODIGO) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE FIREV_REVISI ADD CONSTRAINT FK_FIREV_FICUE_REVISIONES FOREIGN KEY (FICUE_CODIGO)
      REFERENCES FICUE_CUENTA (FICUE_CODIGO) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE FIREV_REVISI ADD CONSTRAINT FK_FIREV_PEPER_REVISOR FOREIGN KEY (PEPER_CODIGO)
      REFERENCES PEPER_PERSON (PEPER_CODIGO) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE GEOIM_ORDENI ADD CONSTRAINT FK_GEOIM_GEEPI_ORDENESIMAGEN FOREIGN KEY (GEEPI_CODIGO)
      REFERENCES GEEPI_EPISOD (GEEPI_CODIGO) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE GEOIM_ORDENI ADD CONSTRAINT FK_GEOIM_PEPER_SOLICITANTEIMAGEN FOREIGN KEY (PEPER_CODIGO)
      REFERENCES PEPER_PERSON (PEPER_CODIGO) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE GEOIM_ORDENI ADD CONSTRAINT FK_GEOIM_GETIP_TIPOESTUDIO FOREIGN KEY (GETIP_CODIGO)
      REFERENCES GETIP_TIPOES (GETIP_CODIGO) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE GEPIM_PROGRA ADD CONSTRAINT FK_GEPIM_GEOIM_PROGRAMACIONES FOREIGN KEY (GEOIM_CODIGO)
      REFERENCES GEOIM_ORDENI (GEOIM_CODIGO) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE GEPIM_PROGRA ADD CONSTRAINT FK_GEPIM_GEREC_RESERVASRECURSO FOREIGN KEY (GEREC_CODIGO)
      REFERENCES GEREC_RECURS (GEREC_CODIGO) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE GEINF_INFORM ADD CONSTRAINT FK_GEINF_GEOIM_INFORME FOREIGN KEY (GEOIM_CODIGO)
      REFERENCES GEOIM_ORDENI (GEOIM_CODIGO) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE GEVII_VERSIO ADD CONSTRAINT FK_GEVII_GEINF_VERSIONESIMAGEN FOREIGN KEY (GEINF_CODIGO)
      REFERENCES GEINF_INFORM (GEINF_CODIGO) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE GEVII_VERSIO ADD CONSTRAINT FK_GEVII_PEPER_AUTORIMAGEN FOREIGN KEY (PEPER_CODIGO)
      REFERENCES PEPER_PERSON (PEPER_CODIGO) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE GEADJ_ADJUNT ADD CONSTRAINT FK_GEADJ_GEVII_ADJUNTOSIMAGEN FOREIGN KEY (GEVII_CODIGO)
      REFERENCES GEVII_VERSIO (GEVII_CODIGO) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE GESOL_SOLICI ADD CONSTRAINT FK_GESOL_GEEPI_SOLICITUDESCIRUGIA FOREIGN KEY (GEEPI_CODIGO)
      REFERENCES GEEPI_EPISOD (GEEPI_CODIGO) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE GESOL_SOLICI ADD CONSTRAINT FK_GESOL_PEPER_MEDICOSOLICITANTE FOREIGN KEY (PEPER_CODIGO)
      REFERENCES PEPER_PERSON (PEPER_CODIGO) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE GESOL_SOLICI ADD CONSTRAINT FK_GESOL_GETPR_TIPOPROCEDIMIENTO FOREIGN KEY (GETPR_CODIGO)
      REFERENCES GETPR_TIPOPR (GETPR_CODIGO) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE GEPQU_PROGRA ADD CONSTRAINT FK_GEPQU_GESOL_PROGRAMACIONESCIRUGIA FOREIGN KEY (GESOL_CODIGO)
      REFERENCES GESOL_SOLICI (GESOL_CODIGO) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE GEPQU_PROGRA ADD CONSTRAINT FK_GEPQU_GESAL_SALA FOREIGN KEY (GESAL_CODIGO)
      REFERENCES GESAL_SALAQU (GESAL_CODIGO) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE GERQU_RESERV ADD CONSTRAINT FK_GERQU_GEPQU_RESERVASEXCLUSIVAS FOREIGN KEY (GEPQU_CODIGO)
      REFERENCES GEPQU_PROGRA (GEPQU_CODIGO) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE GEPAQ_PARTIC ADD CONSTRAINT FK_GEPAQ_GEPQU_PARTICIPANTES FOREIGN KEY (GEPQU_CODIGO)
      REFERENCES GEPQU_PROGRA (GEPQU_CODIGO) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE GEPAQ_PARTIC ADD CONSTRAINT FK_GEPAQ_PEPER_PROFESIONALPARTICIPANTE FOREIGN KEY (PEPER_CODIGO)
      REFERENCES PEPER_PERSON (PEPER_CODIGO) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE GEIQU_INFORM ADD CONSTRAINT FK_GEIQU_GESOL_DOCUMENTACION FOREIGN KEY (GESOL_CODIGO)
      REFERENCES GESOL_SOLICI (GESOL_CODIGO) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE GEVIQ_VERSIO ADD CONSTRAINT FK_GEVIQ_GEIQU_VERSIONESCIRUGIA FOREIGN KEY (GEIQU_CODIGO)
      REFERENCES GEIQU_INFORM (GEIQU_CODIGO) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE GEVIQ_VERSIO ADD CONSTRAINT FK_GEVIQ_PEPER_AUTORCIRUGIA FOREIGN KEY (PEPER_CODIGO)
      REFERENCES PEPER_PERSON (PEPER_CODIGO) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE GEADJ_ADJUNT ADD CONSTRAINT FK_GEADJ_GEVIQ_ADJUNTOSCIRUGIA FOREIGN KEY (GEVIQ_CODIGO)
      REFERENCES GEVIQ_VERSIO (GEVIQ_CODIGO) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE GEADJ_ADJUNT ADD CONSTRAINT FK_GEADJ_GENOT_ADJUNTOSNOTA FOREIGN KEY (GENOT_CODIGO)
      REFERENCES GENOT_NOTACL (GENOT_CODIGO) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE GECRE_CONSUL ADD CONSTRAINT FK_GECRE_GEDEF_CONSULTAS FOREIGN KEY (GEDEF_CODIGO)
      REFERENCES GEDEF_DEFINI (GEDEF_CODIGO) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE GECRE_CONSUL ADD CONSTRAINT FK_GECRE_SEUSU_CONSULTAAUTORIZADA FOREIGN KEY (SEUSU_CODIGO)
      REFERENCES SEUSU_USUARI (SEUSU_CODIGO) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE GEEXP_EXPORT ADD CONSTRAINT FK_GEEXP_GECRE_EXPORTACIONES FOREIGN KEY (GECRE_CODIGO)
      REFERENCES GECRE_CONSUL (GECRE_CODIGO) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE SEEVE_EVENTO ADD CONSTRAINT FK_SEEVE_SEUSU_ACTOREVENTO FOREIGN KEY (SEUSU_CODIGO)
      REFERENCES SEUSU_USUARI (SEUSU_CODIGO) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE SEEVE_EVENTO ADD CONSTRAINT FK_SEEVE_SEAUT_AMPARAACCESO FOREIGN KEY (SEAUT_CODIGO)
      REFERENCES SEAUT_AUTORI (SEAUT_CODIGO) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE SEAUT_AUTORI ADD CONSTRAINT FK_SEAUT_SEUSU_AUDITORAUTORIZADO FOREIGN KEY (SEUSU_CODIGO_AUDITO)
      REFERENCES SEUSU_USUARI (SEUSU_CODIGO) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE SEUSU_USUARI ADD CONSTRAINT FK_SEUSU_SEEST_ESTADOCUENTA FOREIGN KEY (SEEST_CODIGO)
      REFERENCES SEEST_ESTADO (SEEST_CODIGO) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE SEOPC_OPCION ADD CONSTRAINT FK_SEOPC_SESIS_OPERACIONESSISTEMA FOREIGN KEY (SESIS_CODIGO)
      REFERENCES SESIS_SISTEM (SESIS_CODIGO) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE SEPER_PERFIL ADD CONSTRAINT FK_SEPER_SEEST_ESTADOPERFIL FOREIGN KEY (SEEST_CODIGO)
      REFERENCES SEEST_ESTADO (SEEST_CODIGO) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE SEOPC_OPCION ADD CONSTRAINT FK_SEOPC_SEEST_ESTADOOPCION FOREIGN KEY (SEEST_CODIGO)
      REFERENCES SEEST_ESTADO (SEEST_CODIGO) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE PEPER_PERSON ADD CONSTRAINT FK_PEPER_PEGEN_GENEROPERSONAL FOREIGN KEY (PEGEN_CODIGO)
      REFERENCES PEGEN_GENERO (PEGEN_CODIGO) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE PEPER_PERSON ADD CONSTRAINT FK_PEPER_PEEST_ESTADOCIVILPERSONAL FOREIGN KEY (PEEST_CODIGO)
      REFERENCES PEEST_ESTADO (PEEST_CODIGO) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE PEPER_PERSON ADD CONSTRAINT FK_PEPER_PEDEP_DEPARTAMENTOPERSONAL FOREIGN KEY (PEDEP_CODIGO)
      REFERENCES PEDEP_DEPART (PEDEP_CODIGO) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE PEPER_PERSON ADD CONSTRAINT FK_PEPER_PECAR_CARGOPERSONAL FOREIGN KEY (PECAR_CODIGO)
      REFERENCES PECAR_CARGO (PECAR_CODIGO) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE GEPAC_PACIEN ADD CONSTRAINT FK_GEPAC_PEGEN_GENEROPACIENTE FOREIGN KEY (PEGEN_CODIGO)
      REFERENCES PEGEN_GENERO (PEGEN_CODIGO) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE GEPAC_PACIEN ADD CONSTRAINT FK_GEPAC_PEEST_ESTADOCIVILPACIENTE FOREIGN KEY (PEEST_CODIGO)
      REFERENCES PEEST_ESTADO (PEEST_CODIGO) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE PEHAB_HABILI ADD CONSTRAINT FK_PEHAB_PEPER_HABILITACIONES FOREIGN KEY (PEPER_CODIGO)
      REFERENCES PEPER_PERSON (PEPER_CODIGO) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE PEHAB_HABILI ADD CONSTRAINT FK_PEHAB_PEESP_PROFESIONALESHABILITADOS FOREIGN KEY (PEESP_CODIGO)
      REFERENCES PEESP_ESPECI (PEESP_CODIGO) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE GECIT_CITA ADD CONSTRAINT FK_GECIT_PEESP_ESPECIALIDADCITA FOREIGN KEY (PEESP_CODIGO)
      REFERENCES PEESP_ESPECI (PEESP_CODIGO) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE GECIT_CITA ADD CONSTRAINT FK_GECIT_GECIT_REPROGRAMA FOREIGN KEY (GECIT_CODIGO_REPROG)
      REFERENCES GECIT_CITA (GECIT_CODIGO) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE GEDOL_DETALL ADD CONSTRAINT FK_GEDOL_GEORD_EXAMENESORDEN FOREIGN KEY (GEORD_CODIGO)
      REFERENCES GEORD_ORDENL (GEORD_CODIGO) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE GERES_RESULT ADD CONSTRAINT FK_GERES_PEPER_REGISTRARESULTADO FOREIGN KEY (PEPER_CODIGO)
      REFERENCES PEPER_PERSON (PEPER_CODIGO) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE GEMUE_MUESTR ADD CONSTRAINT FK_GEMUE_PEPER_RECIBEMUESTRA FOREIGN KEY (PEPER_CODIGO)
      REFERENCES PEPER_PERSON (PEPER_CODIGO) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE GEACT_ACTUAC ADD CONSTRAINT FK_GEACT_GEEPI_ACTUACIONES FOREIGN KEY (GEEPI_CODIGO)
      REFERENCES GEEPI_EPISOD (GEEPI_CODIGO) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE GEACT_ACTUAC ADD CONSTRAINT FK_GEACT_PEPER_AUTORACTUACION FOREIGN KEY (PEPER_CODIGO)
      REFERENCES PEPER_PERSON (PEPER_CODIGO) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE GEPCA_PREPAR ADD CONSTRAINT FK_GEPCA_GECAM_PREPARACIONES FOREIGN KEY (GECAM_CODIGO)
      REFERENCES GECAM_CAMA (GECAM_CODIGO) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE GEPCA_PREPAR ADD CONSTRAINT FK_GEPCA_PEPER_CONFIRMAPREPARACION FOREIGN KEY (PEPER_CODIGO)
      REFERENCES PEPER_PERSON (PEPER_CODIGO) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE GEMCA_MOVIMI ADD CONSTRAINT FK_GEMCA_PEPER_REGISTRAMOVIMIENTOCAMA FOREIGN KEY (PEPER_CODIGO)
      REFERENCES PEPER_PERSON (PEPER_CODIGO) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE GEMCA_MOVIMI ADD CONSTRAINT FK_GEMCA_GEMCA_TRASLADOANTERIOR FOREIGN KEY (GEMCA_CODIGO_ANTERI)
      REFERENCES GEMCA_MOVIMI (GEMCA_CODIGO) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE GEEST_ESTANC ADD CONSTRAINT FK_GEEST_GEUNI_UNIDADADMISION FOREIGN KEY (GEUNI_CODIGO)
      REFERENCES GEUNI_UNIDAD (GEUNI_CODIGO) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE GEEST_ESTANC ADD CONSTRAINT FK_GEEST_PEPER_REGISTRAADMISION FOREIGN KEY (PEPER_CODIGO)
      REFERENCES PEPER_PERSON (PEPER_CODIGO) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE GERCA_RESERV ADD CONSTRAINT FK_GERCA_PEPER_REGISTRARESERVACAMA FOREIGN KEY (PEPER_CODIGO)
      REFERENCES PEPER_PERSON (PEPER_CODIGO) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE GEDEV_DEVOLU ADD CONSTRAINT FK_GEDEV_PEPER_AUTORIZADEVOLUCION FOREIGN KEY (PEPER_CODIGO)
      REFERENCES PEPER_PERSON (PEPER_CODIGO) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE FIPAG_PAGO ADD CONSTRAINT FK_FIPAG_SEUSU_REGISTRAPAGO FOREIGN KEY (SEUSU_CODIGO)
      REFERENCES SEUSU_USUARI (SEUSU_CODIGO) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE FICMP_COMPEN ADD CONSTRAINT FK_FICMP_SEUSU_REGISTRACOMPENSACION FOREIGN KEY (SEUSU_CODIGO)
      REFERENCES SEUSU_USUARI (SEUSU_CODIGO) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE FITAR_TARIFA ADD CONSTRAINT FK_FITAR_SEUSU_AUTORIZATARIFA FOREIGN KEY (SEUSU_CODIGO)
      REFERENCES SEUSU_USUARI (SEUSU_CODIGO) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE SEAUT_AUTORI ADD CONSTRAINT FK_SEAUT_SEUSU_APRUEBAACCESOEXCEPCIONAL FOREIGN KEY (SEUSU_CODIGO_APRUEB)
      REFERENCES SEUSU_USUARI (SEUSU_CODIGO) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE SEUXP_USUPER ADD CONSTRAINT FK_SEUXP_SEUSU_ASIGNAPERFIL FOREIGN KEY (SEUSU_CODIGO_ASIGNA)
      REFERENCES SEUSU_USUARI (SEUSU_CODIGO) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE SEOXP_OPCPER ADD CONSTRAINT FK_SEOXP_SEUSU_CONCEDEOPERACION FOREIGN KEY (SEUSU_CODIGO)
      REFERENCES SEUSU_USUARI (SEUSU_CODIGO) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE GEDEF_DEFINI ADD CONSTRAINT FK_GEDEF_SESIS_REPORTESMODULO FOREIGN KEY (SESIS_CODIGO)
      REFERENCES SESIS_SISTEM (SESIS_CODIGO) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE GEPAR_PARAME ADD CONSTRAINT FK_GEPAR_SESIS_PARAMETROSMODULO FOREIGN KEY (SESIS_CODIGO)
      REFERENCES SESIS_SISTEM (SESIS_CODIGO) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE GEREP_RESPON ADD CONSTRAINT FK_GEREP_GEEPI_RESPONSABLESEPISODIO FOREIGN KEY (GEEPI_CODIGO)
      REFERENCES GEEPI_EPISOD (GEEPI_CODIGO) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE GEREP_RESPON ADD CONSTRAINT FK_GEREP_PEPER_RESPONSABLEDE FOREIGN KEY (PEPER_CODIGO)
      REFERENCES PEPER_PERSON (PEPER_CODIGO) ON DELETE RESTRICT ON UPDATE RESTRICT;

