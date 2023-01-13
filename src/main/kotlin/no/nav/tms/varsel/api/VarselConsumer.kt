@file:UseSerializers(ZonedDateTimeSerializer::class)

package no.nav.tms.varsel.api

import io.ktor.client.HttpClient
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import no.nav.tms.token.support.tokendings.exchange.TokendingsService
import no.nav.tms.varsel.api.config.ZonedDateTimeSerializer
import no.nav.tms.varsel.api.config.get
import java.time.ZonedDateTime

class VarselConsumer(
    private val client: HttpClient,
    private val eventHandlerBaseURL: String,
    private val eventhandlerClientId: String,
    private val tokendingsService: TokendingsService,
) {
    suspend fun getInaktiveVarsler(accessToken: String, loginLevel: Int): List<InaktivtVarsel> {
        val eventhandlerToken = tokendingsService.exchangeToken(accessToken, targetApp = eventhandlerClientId)

        return getInaktiveBeskjeder(eventhandlerToken).map { InaktivtVarsel.fromBeskjed(it, loginLevel) } +
                getInaktiveOppgaver(eventhandlerToken).map { InaktivtVarsel.fromOppgave(it, loginLevel) } +
                getInaktiveInnbokser(eventhandlerToken).map { InaktivtVarsel.fromInnboks(it, loginLevel) }
    }

    private suspend fun getInaktiveBeskjeder(eventhandlerToken: String): List<Beskjed> =
        client.get("$eventHandlerBaseURL/fetch/beskjed/inaktive", eventhandlerToken)

    private suspend fun getInaktiveOppgaver(eventhandlerToken: String): List<Oppgave> =
        client.get("$eventHandlerBaseURL/fetch/oppgave/inaktive", eventhandlerToken)

    private suspend fun getInaktiveInnbokser(eventhandlerToken: String): List<Innboks> =
        client.get("$eventHandlerBaseURL/fetch/innboks/inaktive", eventhandlerToken)
}

@Serializable
data class Beskjed(
    val fodselsnummer: String,
    val eventId: String,
    val eventTidspunkt: ZonedDateTime,
    val forstBehandlet: ZonedDateTime,
    val sikkerhetsnivaa: Int,
    val sistOppdatert: ZonedDateTime,
    val synligFremTil: ZonedDateTime?,
    val tekst: String,
    val link: String,
    val aktiv: Boolean,
    val eksternVarslingSendt: Boolean,
    val eksternVarslingKanaler: List<String>
)

@Serializable
data class Oppgave(
    val fodselsnummer: String,
    val eventId: String,
    val forstBehandlet: ZonedDateTime,
    val sikkerhetsnivaa: Int,
    val sistOppdatert: ZonedDateTime,
    val tekst: String,
    val link: String,
    val aktiv: Boolean,
    val eksternVarslingSendt: Boolean,
    val eksternVarslingKanaler: List<String>
)

@Serializable
data class Innboks(
    val forstBehandlet: ZonedDateTime,
    val fodselsnummer: String,
    val eventId: String,
    val tekst: String,
    val link: String,
    val sikkerhetsnivaa: Int,
    val sistOppdatert: ZonedDateTime,
    val aktiv: Boolean,
    val eksternVarslingSendt: Boolean,
    val eksternVarslingKanaler: List<String>
)