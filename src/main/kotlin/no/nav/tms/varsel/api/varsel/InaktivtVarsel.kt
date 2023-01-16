@file:UseSerializers(ZonedDateTimeSerializer::class)

package no.nav.tms.varsel.api.varsel

import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import no.nav.tms.varsel.api.ZonedDateTimeSerializer
import java.time.ZonedDateTime

@Serializable
data class InaktivtVarsel(
    val eventId: String,
    val forstBehandlet: ZonedDateTime,
    val type: VarselType,
    val isMasked: Boolean,
    val tekst: String?,
    val eksternVarslingSendt: Boolean,
    val eksternVarslingKanaler: List<String>
) {
    companion object {
        fun fromBeskjed(beskjed: Beskjed, loginLevel: Int) = inaktivtVarsel(
            eventId = beskjed.eventId,
            forstBehandlet = beskjed.forstBehandlet,
            type = VarselType.BESKJED,
            sikkerhetsnivaa = beskjed.sikkerhetsnivaa,
            tekst = beskjed.tekst,
            eksternVarslingSendt = beskjed.eksternVarslingSendt,
            eksternVarslingKanaler = beskjed.eksternVarslingKanaler,
            loginLevel
        )

        fun fromOppgave(oppgave: Oppgave, loginLevel: Int) = inaktivtVarsel(
            eventId = oppgave.eventId,
            forstBehandlet = oppgave.forstBehandlet,
            type = VarselType.OPPGAVE,
            sikkerhetsnivaa = oppgave.sikkerhetsnivaa,
            tekst = oppgave.tekst,
            eksternVarslingSendt = oppgave.eksternVarslingSendt,
            eksternVarslingKanaler = oppgave.eksternVarslingKanaler,
            loginLevel
        )

        fun fromInnboks(innboks: Innboks, loginLevel: Int) = inaktivtVarsel(
            eventId = innboks.eventId,
            forstBehandlet = innboks.forstBehandlet,
            type = VarselType.INNBOKS,
            sikkerhetsnivaa = innboks.sikkerhetsnivaa,
            tekst = innboks.tekst,
            eksternVarslingSendt = innboks.eksternVarslingSendt,
            eksternVarslingKanaler = innboks.eksternVarslingKanaler,
            loginLevel
        )

        private fun inaktivtVarsel(
            eventId: String,
            forstBehandlet: ZonedDateTime,
            type: VarselType,
            sikkerhetsnivaa: Int,
            tekst: String,
            eksternVarslingSendt: Boolean,
            eksternVarslingKanaler: List<String>,
            authLevel: Int
        ): InaktivtVarsel {
            if (sikkerhetsnivaa > authLevel) {
                return InaktivtVarsel(
                    eventId = eventId,
                    forstBehandlet = forstBehandlet,
                    type = type,
                    isMasked = true,
                    tekst = null,
                    eksternVarslingSendt = eksternVarslingSendt,
                    eksternVarslingKanaler = eksternVarslingKanaler
                )
            } else {
                return InaktivtVarsel(
                    eventId = eventId,
                    forstBehandlet = forstBehandlet,
                    type = type,
                    isMasked = false,
                    tekst = tekst,
                    eksternVarslingSendt = eksternVarslingSendt,
                    eksternVarslingKanaler = eksternVarslingKanaler
                )
            }
        }
    }
}

enum class VarselType {
    OPPGAVE,
    BESKJED,
    INNBOKS,
}
