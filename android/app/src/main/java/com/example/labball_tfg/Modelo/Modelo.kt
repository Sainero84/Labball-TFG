package com.example.labball_tfg.Modelo

import com.google.gson.annotations.SerializedName

// Define el modelo o contrato usuario response usado para comunicar la app con la API.
data class UsuarioResponse(
    @SerializedName("id_usuario") val idUsuario: Int,
    @SerializedName("firebase_uid") val firebaseUid: String?,
    val correo: String,
    @SerializedName("es_admin") val esAdmin: Boolean,
    @SerializedName("es_super_admin") val esSuperAdmin: Boolean = false,
    @SerializedName("es_entrenador") val esEntrenador: Boolean = false,
    val telefono: String?,
    val nombre: String? = null,
    @SerializedName(value = "apellido_1", alternate = ["apellido1", "primer_apellido"])
    val apellido1: String? = null,
    @SerializedName("fecha_nacimiento") val fechaNacimiento: String? = null,
    @SerializedName("foto_perfil_url") val fotoPerfilUrl: String?,
    @SerializedName("foto_perfil_mime_type") val fotoPerfilMimeType: String?
)

// Define el modelo o contrato usuario create request usado para comunicar la app con la API.
data class UsuarioCreateRequest(
    val firebase_uid: String,
    val correo: String,
    val nombre: String,
    @SerializedName("apellido_1") val apellido1: String,
    @SerializedName("fecha_nacimiento") val fechaNacimiento: String,
    val telefono: String? = null,
    val foto_perfil_url: String? = null,
    val foto_perfil_mime_type: String? = null
)


// Define el modelo o contrato usuario me response usado para comunicar la app con la API.
data class UsuarioMeResponse(
    @SerializedName("id_usuario") val idUsuario: Int? = null,
    val correo: String,
    @SerializedName("es_admin") val esAdmin: Boolean,
    @SerializedName("es_super_admin") val esSuperAdmin: Boolean = false,
    @SerializedName("es_entrenador") val esEntrenador: Boolean = false,
    val telefono: String?,
    val nombre: String? = null,
    @SerializedName(value = "apellido_1", alternate = ["apellido1", "primer_apellido"])
    val apellido1: String? = null,
    @SerializedName("fecha_nacimiento") val fechaNacimiento: String? = null,
    @SerializedName("foto_perfil_url") val fotoPerfilUrl: String?
)

// Define el modelo o contrato usuario telefono update request usado para comunicar la app con la API.
data class UsuarioTelefonoUpdateRequest(val telefono: String?)

// Define el modelo o contrato usuario perfil update request usado para comunicar la app con la API.
data class UsuarioPerfilUpdateRequest(
    val nombre: String,
    @SerializedName("apellido_1") val apellido1: String,
    @SerializedName("fecha_nacimiento") val fechaNacimiento: String,
    val telefono: String?
)

// Define el modelo o contrato usuario foto perfil update request usado para comunicar la app con la API.
data class UsuarioFotoPerfilUpdateRequest(
    @SerializedName("foto_perfil_url") val fotoPerfilUrl: String,
    @SerializedName("foto_perfil_mime_type") val fotoPerfilMimeType: String?
)

// Define el modelo o contrato auth user response usado para comunicar la app con la API.
data class AuthUserResponse(
    val message: String,
    val usuario: UsuarioResponse
)

// Define el modelo o contrato auth message response usado para comunicar la app con la API.
data class AuthMessageResponse(val message: String)

// Define el modelo o contrato tarifa create request usado para comunicar la app con la API.
data class TarifaCreateRequest(
    @SerializedName("numero_sesiones") val numeroSesiones: Int,
    @SerializedName("precio_total") val precioTotal: Double,
    @SerializedName("precio_por_sesion") val precioPorSesion: Double,
    val activa: Boolean = true
)

// Define el modelo o contrato tarifa update request usado para comunicar la app con la API.
data class TarifaUpdateRequest(
    @SerializedName("numero_sesiones") val numeroSesiones: Int? = null,
    @SerializedName("precio_total") val precioTotal: Double? = null,
    @SerializedName("precio_por_sesion") val precioPorSesion: Double? = null,
    val activa: Boolean? = null
)

// Define el modelo o contrato tarifa message response usado para comunicar la app con la API.
data class TarifaMessageResponse(
    val message: String,
    val tarifa: TarifaResponse
)

// Define el modelo o contrato reserva admin list item response usado para comunicar la app con la API.
data class ReservaAdminListItemResponse(
    @SerializedName("id_reserva") val idReserva: Int,
    val nombre: String,
    val apellidos: String,
    val correo: String,
    @SerializedName("numero_sesiones") val numeroSesiones: Int,
    val pagado: Boolean,
    @SerializedName("tiene_entrenamientos") val tieneEntrenamientos: Boolean
)

// Define el modelo o contrato reserva admin list response usado para comunicar la app con la API.
data class ReservaAdminListResponse(
    val reservas: List<ReservaAdminListItemResponse>
)

// Define el modelo o contrato reserva pagado update request usado para comunicar la app con la API.
data class ReservaPagadoUpdateRequest(val pagado: Boolean)

// Define el modelo o contrato entrenamiento create request usado para comunicar la app con la API.
data class EntrenamientoCreateRequest(
    @SerializedName("id_entrenador") val idEntrenador: Int,
    @SerializedName("id_ubicacion") val idUbicacion: Int,
    @SerializedName("nombre_entrenador") val nombreEntrenador: String? = null,
    val ubicacion: String? = null,
    @SerializedName("hora_inicio") val horaInicio: String,
    @SerializedName("hora_fin") val horaFin: String,
    @SerializedName("id_jugador") val idJugador: Int,
    @SerializedName("id_usuario") val idUsuario: Int
)

// Define el modelo o contrato entrenamiento asignacion request usado para comunicar la app con la API.
data class EntrenamientoAsignacionRequest(
    @SerializedName("id_entrenador") val idEntrenador: Int,
    @SerializedName("id_ubicacion") val idUbicacion: Int,
    @SerializedName("hora_inicio") val horaInicio: String,
    @SerializedName("hora_fin") val horaFin: String
)

// Define el modelo o contrato reserva entrenamientos asignar request usado para comunicar la app con la API.
data class ReservaEntrenamientosAsignarRequest(
    val entrenamientos: List<EntrenamientoAsignacionRequest>
)

// Define el modelo o contrato entrenamiento update request usado para comunicar la app con la API.
data class EntrenamientoUpdateRequest(
    @SerializedName("id_entrenador") val idEntrenador: Int? = null,
    @SerializedName("id_ubicacion") val idUbicacion: Int? = null,
    @SerializedName("hora_inicio") val horaInicio: String? = null,
    @SerializedName("hora_fin") val horaFin: String? = null,
    @SerializedName("id_jugador") val idJugador: Int? = null,
    @SerializedName("id_usuario") val idUsuario: Int? = null
)

// Define el modelo o contrato entrenamiento response usado para comunicar la app con la API.
data class EntrenamientoResponse(
    @SerializedName("id_entrenamiento") val idEntrenamiento: Int,
    @SerializedName("id_entrenador") val idEntrenador: Int,
    @SerializedName("nombre_entrenador") val nombreEntrenador: String,
    @SerializedName("id_ubicacion") val idUbicacion: Int,
    val ubicacion: String,
    @SerializedName("hora_inicio") val horaInicio: String,
    @SerializedName("hora_fin") val horaFin: String,
    @SerializedName("id_jugador") val idJugador: Int?,
    @SerializedName("id_usuario") val idUsuario: Int?
)

// Define el modelo o contrato entrenamiento list response usado para comunicar la app con la API.
data class EntrenamientoListResponse(
    val entrenamientos: List<EntrenamientoResponse>
)

// Define el modelo o contrato entrenamiento message response usado para comunicar la app con la API.
data class EntrenamientoMessageResponse(
    val message: String,
    val entrenamiento: EntrenamientoResponse
)

// Define el modelo o contrato reserva entrenamientos asignar response usado para comunicar la app con la API.
data class ReservaEntrenamientosAsignarResponse(
    val message: String?,
    val entrenamientos: List<EntrenamientoResponse>?
)

// Define el modelo o contrato video list item response usado para comunicar la app con la API.
data class VideoListItemResponse(
    @SerializedName("id_media") val idMedia: Int,
    val titulo: String,
    val descripcion: String?,
    @SerializedName("url_miniatura") val urlMiniatura: String?
)

// Define el modelo o contrato video list response usado para comunicar la app con la API.
data class VideoListResponse(val videos: List<VideoListItemResponse>)

// Define el modelo o contrato video detail response usado para comunicar la app con la API.
data class VideoDetailResponse(
    @SerializedName("id_media") val idMedia: Int,
    val titulo: String,
    val descripcion: String?,
    @SerializedName("url_archivo") val urlArchivo: String,
    @SerializedName("mime_type") val mimeType: String?
)

// Define el modelo o contrato media create request usado para comunicar la app con la API.
data class MediaCreateRequest(
    @SerializedName("id_usuario") val idUsuario: Int,
    val titulo: String,
    val descripcion: String?,
    @SerializedName("url_archivo") val urlArchivo: String,
    @SerializedName("url_miniatura") val urlMiniatura: String?,
    @SerializedName("mime_type") val mimeType: String?
)

// Define el modelo o contrato media update request usado para comunicar la app con la API.
data class MediaUpdateRequest(
    @SerializedName("id_usuario") val idUsuario: Int? = null,
    val titulo: String? = null,
    val descripcion: String? = null,
    @SerializedName("url_archivo") val urlArchivo: String? = null,
    @SerializedName("url_miniatura") val urlMiniatura: String? = null,
    @SerializedName("mime_type") val mimeType: String? = null
)

// Define el modelo o contrato media response usado para comunicar la app con la API.
data class MediaResponse(
    @SerializedName("id_media") val idMedia: Int,
    @SerializedName("id_usuario") val idUsuario: Int,
    val titulo: String,
    val descripcion: String?,
    @SerializedName("url_archivo") val urlArchivo: String,
    @SerializedName("url_miniatura") val urlMiniatura: String?,
    @SerializedName("mime_type") val mimeType: String?
)

// Define el modelo o contrato media message response usado para comunicar la app con la API.
data class MediaMessageResponse(
    val message: String,
    val media: MediaResponse
)

// Define el modelo o contrato jugador create request usado para comunicar la app con la API.
data class JugadorCreateRequest(
    @SerializedName("id_usuario") val idUsuario: Int,
    val nombre: String,
    val apellidos: String,
    val peso: Double? = null,
    val altura: Double? = null,
    val posicion: String? = null,
    val tiro: Int? = null,
    val fisico: Int? = null,
    val bote: Int? = null,
    val pase: Int? = null,
    val defensa: Int? = null,
    val velocidad: Int? = null
)

// Define el modelo o contrato jugador update request usado para comunicar la app con la API.
data class JugadorUpdateRequest(
    @SerializedName("id_usuario") val idUsuario: Int? = null,
    val nombre: String? = null,
    val apellidos: String? = null,
    val peso: Double? = null,
    val altura: Double? = null,
    val posicion: String? = null,
    val tiro: Int? = null,
    val fisico: Int? = null,
    val bote: Int? = null,
    val pase: Int? = null,
    val defensa: Int? = null,
    val velocidad: Int? = null
)

// Define el modelo o contrato jugador estadisticas update request usado para comunicar la app con la API.
data class JugadorEstadisticasUpdateRequest(
    val tiro: Int? = null,
    val fisico: Int? = null,
    val bote: Int? = null,
    val pase: Int? = null,
    val defensa: Int? = null,
    val velocidad: Int? = null
)

// Define el modelo o contrato jugador entrenador asignado response usado para comunicar la app con la API.
data class JugadorEntrenadorAsignadoResponse(
    @SerializedName("id_entrenador") val idEntrenador: Int,
    val nombre: String
)

// Define el modelo o contrato jugador ubicacion asignada response usado para comunicar la app con la API.
data class JugadorUbicacionAsignadaResponse(
    @SerializedName("id_ubicacion") val idUbicacion: Int,
    val nombre: String
)

// Define el modelo o contrato jugador response usado para comunicar la app con la API.
data class JugadorResponse(
    @SerializedName("id_jugador") val idJugador: Int,
    @SerializedName("id_usuario") val idUsuario: Int,
    val nombre: String,
    val apellidos: String,
    val peso: Double?,
    val altura: Double?,
    val posicion: String?,
    val tiro: Int?,
    val fisico: Int?,
    val bote: Int?,
    val pase: Int?,
    val defensa: Int?,
    val velocidad: Int?,
    @SerializedName("foto_perfil_url") val fotoPerfilUrl: String? = null,
    @SerializedName("id_entrenador") val idEntrenador: Int? = null,
    @SerializedName("nombre_entrenador") val nombreEntrenador: String? = null,
    @SerializedName("id_ubicacion") val idUbicacion: Int? = null,
    val ubicacion: String? = null,
    val entrenadores: List<JugadorEntrenadorAsignadoResponse> = emptyList(),
    val ubicaciones: List<JugadorUbicacionAsignadaResponse> = emptyList()
)

// Define el modelo o contrato jugador list response usado para comunicar la app con la API.
data class JugadorListResponse(val jugadores: List<JugadorResponse>)

// Define el modelo o contrato jugador message response usado para comunicar la app con la API.
data class JugadorMessageResponse(
    val message: String,
    val jugador: JugadorResponse
)

// Define el modelo o contrato reserva create request usado para comunicar la app con la API.
data class ReservaCreateRequest(
    @SerializedName("numero_sesiones") val numeroSesiones: Int,
    val nombre: String,
    val apellidos: String,
    val dni: String,
    @SerializedName("fecha_nacimiento") val fechaNacimiento: String,
    val correo: String,
    val telefono: String?,
    val club: String?,
    val categoria: String?,
    @SerializedName("codigo_descuento") val codigoDescuento: String?,
    val semanas: List<Int>
)

// Define el modelo o contrato reserva response usado para comunicar la app con la API.
data class ReservaResponse(
    @SerializedName("id_reserva") val idReserva: Int,
    @SerializedName("id_usuario") val idUsuario: Int,
    @SerializedName("numero_sesiones") val numeroSesiones: Int,
    val nombre: String,
    val apellidos: String,
    val dni: String,
    @SerializedName("fecha_nacimiento") val fechaNacimiento: String,
    val correo: String,
    val telefono: String?,
    val club: String?,
    val categoria: String?,
    @SerializedName("codigo_descuento") val codigoDescuento: String?,
    val semanas: List<Int>,
    @SerializedName("precio_sin_descuento") val precioSinDescuento: Double,
    @SerializedName("descuento_aplicado") val descuentoAplicado: Double,
    @SerializedName("precio_final") val precioFinal: Double,
    val pagado: Boolean,
    @SerializedName("id_jugador") val idJugador: Int?
)

// Define el modelo o contrato reserva list response usado para comunicar la app con la API.
data class ReservaListResponse(
    val reservas: List<ReservaResponse>
)

// Define el modelo o contrato reserva message response usado para comunicar la app con la API.
data class ReservaMessageResponse(
    val message: String,
    val reserva: ReservaResponse
)

// Define el modelo o contrato reserva preview request usado para comunicar la app con la API.
data class ReservaPreviewRequest(
    @SerializedName("numero_sesiones") val numeroSesiones: Int,
    @SerializedName("codigo_descuento") val codigoDescuento: String? = null
)

// Define el modelo o contrato reserva preview response usado para comunicar la app con la API.
data class ReservaPreviewResponse(
    @SerializedName("numero_sesiones") val numeroSesiones: Int,
    @SerializedName("precio_sin_descuento") val precioSinDescuento: Double,
    @SerializedName("descuento_aplicado") val descuentoAplicado: Double,
    @SerializedName("precio_final") val precioFinal: Double,
    @SerializedName("codigo_descuento") val codigoDescuento: String?
)

// Define el modelo o contrato tarifa response usado para comunicar la app con la API.
data class TarifaResponse(
    @SerializedName("id_tarifa") val idTarifa: Int,
    @SerializedName("numero_sesiones") val numeroSesiones: Int,
    @SerializedName("precio_total") val precioTotal: Double,
    @SerializedName("precio_por_sesion") val precioPorSesion: Double,
    val activa: Boolean
)

// Define el modelo o contrato tarifa list response usado para comunicar la app con la API.
data class TarifaListResponse(
    val tarifas: List<TarifaResponse>
)

// Define el modelo o contrato entrenador response usado para comunicar la app con la API.
data class EntrenadorResponse(
    @SerializedName("id_entrenador") val idEntrenador: Int,
    val nombre: String,
    val activo: Boolean
)

// Define el modelo o contrato entrenador list response usado para comunicar la app con la API.
data class EntrenadorListResponse(
    val entrenadores: List<EntrenadorResponse>
)

// Define el modelo o contrato ubicacion create request usado para comunicar la app con la API.
data class UbicacionCreateRequest(
    val nombre: String
)

// Define el modelo o contrato ubicacion response usado para comunicar la app con la API.
data class UbicacionResponse(
    @SerializedName("id_ubicacion") val idUbicacion: Int,
    val nombre: String,
    val activo: Boolean
)

// Define el modelo o contrato ubicacion list response usado para comunicar la app con la API.
data class UbicacionListResponse(
    val ubicaciones: List<UbicacionResponse>
)

// Define el modelo o contrato ubicacion message response usado para comunicar la app con la API.
data class UbicacionMessageResponse(
    val message: String,
    val ubicacion: UbicacionResponse
)

// Define el modelo o contrato descuento admin create request usado para comunicar la app con la API.
data class DescuentoAdminCreateRequest(
    val porcentaje: Double,
    val codigo: String? = null
)

// Define el modelo o contrato descuento update request usado para comunicar la app con la API.
data class DescuentoUpdateRequest(
    val porcentaje: Double? = null,
    val codigo: String? = null
)

// Define el modelo o contrato descuento response usado para comunicar la app con la API.
data class DescuentoResponse(
    @SerializedName("id_descuento") val idDescuento: Int,
    val codigo: String,
    val porcentaje: Double
)

// Define el modelo o contrato descuento list response usado para comunicar la app con la API.
data class DescuentoListResponse(
    val descuentos: List<DescuentoResponse>
)

// Define el modelo o contrato descuento message response usado para comunicar la app con la API.
data class DescuentoMessageResponse(
    val message: String,
    val descuento: DescuentoResponse
)

// Define el modelo o contrato admin delete message response usado para comunicar la app con la API.
data class AdminDeleteMessageResponse(
    val message: String
)
// Define el modelo o contrato admin usuario create request usado para comunicar la app con la API.
data class AdminUsuarioCreateRequest(
    val correo: String,
    @SerializedName("es_admin") val esAdmin: Boolean = false,
    @SerializedName("es_super_admin") val esSuperAdmin: Boolean = false,
    @SerializedName("es_entrenador") val esEntrenador: Boolean = false
)

// Define el modelo o contrato admin usuario rol update request usado para comunicar la app con la API.
data class AdminUsuarioRolUpdateRequest(
    @SerializedName("es_admin") val esAdmin: Boolean,
    @SerializedName("es_super_admin") val esSuperAdmin: Boolean = false,
    @SerializedName("es_entrenador") val esEntrenador: Boolean = false
)

// Define el modelo o contrato admin usuario response usado para comunicar la app con la API.
data class AdminUsuarioResponse(
    @SerializedName("id_usuario") val idUsuario: Int,
    val correo: String,
    @SerializedName("es_admin") val esAdmin: Boolean,
    @SerializedName("es_super_admin") val esSuperAdmin: Boolean = false,
    @SerializedName("es_entrenador") val esEntrenador: Boolean = false,
    val telefono: String?,
    val nombre: String? = null,
    @SerializedName(value = "apellido_1", alternate = ["apellido1", "primer_apellido"])
    val apellido1: String? = null,
    @SerializedName("fecha_nacimiento") val fechaNacimiento: String? = null,
    @SerializedName("foto_perfil_url") val fotoPerfilUrl: String? = null
)

// Define el modelo o contrato admin usuario list response usado para comunicar la app con la API.
data class AdminUsuarioListResponse(
    val usuarios: List<AdminUsuarioResponse>
)

// Define el modelo o contrato admin usuario message response usado para comunicar la app con la API.
data class AdminUsuarioMessageResponse(
    val message: String,
    val usuario: AdminUsuarioResponse
)

// Define el modelo o contrato admin usuario delete response usado para comunicar la app con la API.
data class AdminUsuarioDeleteResponse(
    val message: String
)


