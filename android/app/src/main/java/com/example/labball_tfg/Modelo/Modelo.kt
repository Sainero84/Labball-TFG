package com.example.labball_tfg.Modelo

import com.google.gson.annotations.SerializedName

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

data class UsuarioTelefonoUpdateRequest(val telefono: String?)

data class UsuarioPerfilUpdateRequest(
    val nombre: String,
    @SerializedName("apellido_1") val apellido1: String,
    @SerializedName("fecha_nacimiento") val fechaNacimiento: String,
    val telefono: String?
)

data class UsuarioFotoPerfilUpdateRequest(
    @SerializedName("foto_perfil_url") val fotoPerfilUrl: String,
    @SerializedName("foto_perfil_mime_type") val fotoPerfilMimeType: String?
)

data class AuthUserResponse(
    val message: String,
    val usuario: UsuarioResponse
)

data class AuthMessageResponse(val message: String)

data class TarifaCreateRequest(
    @SerializedName("numero_sesiones") val numeroSesiones: Int,
    @SerializedName("precio_total") val precioTotal: Double,
    @SerializedName("precio_por_sesion") val precioPorSesion: Double,
    val activa: Boolean = true
)

data class TarifaUpdateRequest(
    @SerializedName("numero_sesiones") val numeroSesiones: Int? = null,
    @SerializedName("precio_total") val precioTotal: Double? = null,
    @SerializedName("precio_por_sesion") val precioPorSesion: Double? = null,
    val activa: Boolean? = null
)

data class TarifaMessageResponse(
    val message: String,
    val tarifa: TarifaResponse
)

data class ReservaAdminListItemResponse(
    @SerializedName("id_reserva") val idReserva: Int,
    val nombre: String,
    val apellidos: String,
    val correo: String,
    @SerializedName("numero_sesiones") val numeroSesiones: Int,
    val pagado: Boolean,
    @SerializedName("tiene_entrenamientos") val tieneEntrenamientos: Boolean
)

data class ReservaAdminListResponse(
    val reservas: List<ReservaAdminListItemResponse>
)

data class ReservaPagadoUpdateRequest(val pagado: Boolean)

data class EntrenamientoCreateRequest(
    @SerializedName("nombre_entrenador") val nombreEntrenador: String,
    val ubicacion: String,
    @SerializedName("hora_inicio") val horaInicio: String,
    @SerializedName("hora_fin") val horaFin: String,
    @SerializedName("id_jugador") val idJugador: Int,
    @SerializedName("id_usuario") val idUsuario: Int
)

data class EntrenamientoAsignacionRequest(
    @SerializedName("nombre_entrenador") val nombreEntrenador: String,
    val ubicacion: String,
    @SerializedName("hora_inicio") val horaInicio: String,
    @SerializedName("hora_fin") val horaFin: String
)

data class ReservaEntrenamientosAsignarRequest(
    val entrenamientos: List<EntrenamientoAsignacionRequest>
)

data class EntrenamientoUpdateRequest(
    @SerializedName("nombre_entrenador") val nombreEntrenador: String? = null,
    val ubicacion: String? = null,
    @SerializedName("hora_inicio") val horaInicio: String? = null,
    @SerializedName("hora_fin") val horaFin: String? = null,
    @SerializedName("id_jugador") val idJugador: Int? = null,
    @SerializedName("id_usuario") val idUsuario: Int? = null
)

data class EntrenamientoResponse(
    @SerializedName("id_entrenamiento") val idEntrenamiento: Int,
    @SerializedName("nombre_entrenador") val nombreEntrenador: String,
    val ubicacion: String,
    @SerializedName("hora_inicio") val horaInicio: String,
    @SerializedName("hora_fin") val horaFin: String,
    @SerializedName("id_jugador") val idJugador: Int?,
    @SerializedName("id_usuario") val idUsuario: Int
)

data class EntrenamientoListResponse(
    val entrenamientos: List<EntrenamientoResponse>
)

data class EntrenamientoMessageResponse(
    val message: String,
    val entrenamiento: EntrenamientoResponse
)

data class ReservaEntrenamientosAsignarResponse(
    val message: String?,
    val entrenamientos: List<EntrenamientoResponse>?
)

data class VideoListItemResponse(
    @SerializedName("id_media") val idMedia: Int,
    val titulo: String,
    val descripcion: String?,
    @SerializedName("url_miniatura") val urlMiniatura: String?
)

data class VideoListResponse(val videos: List<VideoListItemResponse>)

data class VideoDetailResponse(
    @SerializedName("id_media") val idMedia: Int,
    val titulo: String,
    val descripcion: String?,
    @SerializedName("url_archivo") val urlArchivo: String,
    @SerializedName("mime_type") val mimeType: String?
)

data class MediaCreateRequest(
    @SerializedName("id_usuario") val idUsuario: Int,
    val titulo: String,
    val descripcion: String?,
    @SerializedName("url_archivo") val urlArchivo: String,
    @SerializedName("url_miniatura") val urlMiniatura: String?,
    @SerializedName("mime_type") val mimeType: String?
)

data class MediaUpdateRequest(
    @SerializedName("id_usuario") val idUsuario: Int? = null,
    val titulo: String? = null,
    val descripcion: String? = null,
    @SerializedName("url_archivo") val urlArchivo: String? = null,
    @SerializedName("url_miniatura") val urlMiniatura: String? = null,
    @SerializedName("mime_type") val mimeType: String? = null
)

data class MediaResponse(
    @SerializedName("id_media") val idMedia: Int,
    @SerializedName("id_usuario") val idUsuario: Int,
    val titulo: String,
    val descripcion: String?,
    @SerializedName("url_archivo") val urlArchivo: String,
    @SerializedName("url_miniatura") val urlMiniatura: String?,
    @SerializedName("mime_type") val mimeType: String?
)

data class MediaMessageResponse(
    val message: String,
    val media: MediaResponse
)

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

data class JugadorEstadisticasUpdateRequest(
    val tiro: Int? = null,
    val fisico: Int? = null,
    val bote: Int? = null,
    val pase: Int? = null,
    val defensa: Int? = null,
    val velocidad: Int? = null
)

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
    @SerializedName("nombre_entrenador") val nombreEntrenador: String? = null,
    val ubicacion: String? = null
)

data class JugadorListResponse(val jugadores: List<JugadorResponse>)

data class JugadorMessageResponse(
    val message: String,
    val jugador: JugadorResponse
)

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

data class ReservaListResponse(
    val reservas: List<ReservaResponse>
)

data class ReservaMessageResponse(
    val message: String,
    val reserva: ReservaResponse
)

data class ReservaPreviewRequest(
    @SerializedName("numero_sesiones") val numeroSesiones: Int,
    @SerializedName("codigo_descuento") val codigoDescuento: String? = null
)

data class ReservaPreviewResponse(
    @SerializedName("numero_sesiones") val numeroSesiones: Int,
    @SerializedName("precio_sin_descuento") val precioSinDescuento: Double,
    @SerializedName("descuento_aplicado") val descuentoAplicado: Double,
    @SerializedName("precio_final") val precioFinal: Double,
    @SerializedName("codigo_descuento") val codigoDescuento: String?
)

data class TarifaResponse(
    @SerializedName("id_tarifa") val idTarifa: Int,
    @SerializedName("numero_sesiones") val numeroSesiones: Int,
    @SerializedName("precio_total") val precioTotal: Double,
    @SerializedName("precio_por_sesion") val precioPorSesion: Double,
    val activa: Boolean
)

data class TarifaListResponse(
    val tarifas: List<TarifaResponse>
)

data class EntrenadorResponse(
    @SerializedName("id_entrenador") val idEntrenador: Int,
    val nombre: String,
    val activo: Boolean
)

data class EntrenadorListResponse(
    val entrenadores: List<EntrenadorResponse>
)

data class UbicacionCreateRequest(
    val nombre: String
)

data class UbicacionResponse(
    @SerializedName("id_ubicacion") val idUbicacion: Int,
    val nombre: String,
    val activo: Boolean
)

data class UbicacionListResponse(
    val ubicaciones: List<UbicacionResponse>
)

data class UbicacionMessageResponse(
    val message: String,
    val ubicacion: UbicacionResponse
)

data class DescuentoAdminCreateRequest(
    val porcentaje: Double,
    val codigo: String? = null
)

data class DescuentoUpdateRequest(
    val porcentaje: Double? = null,
    val codigo: String? = null
)

data class DescuentoResponse(
    @SerializedName("id_descuento") val idDescuento: Int,
    val codigo: String,
    val porcentaje: Double
)

data class DescuentoListResponse(
    val descuentos: List<DescuentoResponse>
)

data class DescuentoMessageResponse(
    val message: String,
    val descuento: DescuentoResponse
)

data class AdminDeleteMessageResponse(
    val message: String
)
data class AdminUsuarioCreateRequest(
    val correo: String,
    @SerializedName("es_admin") val esAdmin: Boolean = false,
    @SerializedName("es_super_admin") val esSuperAdmin: Boolean = false,
    @SerializedName("es_entrenador") val esEntrenador: Boolean = false
)

data class AdminUsuarioRolUpdateRequest(
    @SerializedName("es_admin") val esAdmin: Boolean,
    @SerializedName("es_super_admin") val esSuperAdmin: Boolean = false,
    @SerializedName("es_entrenador") val esEntrenador: Boolean = false
)

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

data class AdminUsuarioListResponse(
    val usuarios: List<AdminUsuarioResponse>
)

data class AdminUsuarioMessageResponse(
    val message: String,
    val usuario: AdminUsuarioResponse
)

data class AdminUsuarioDeleteResponse(
    val message: String
)


