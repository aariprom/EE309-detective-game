package com.ee309.detectivegame.llm.model

import com.ee309.detectivegame.domain.model.GameState
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

@Serializable
data class EpilogueRequest(
    val case: CaseInfo,
    val criminal: CriminalInfo,
    val victim: VictimInfo,
    val player: PlayerInfoEpilogue,
    val language: String = "en",
    val outcome: String = "WIN"
)

@Serializable
data class CaseInfo(
    val title: String,
    val description: String
)

@Serializable
data class CriminalInfo(
    val name: String,
    val motive: String,
    val methodSummary: String,
    val keyClues: List<String>
)

@Serializable
data class VictimInfo(
    val name: String,
    val role: String
)

@Serializable
data class PlayerInfoEpilogue(
    val role: String
)

@Serializable
data class EpilogueResponse(
    val text: String
)

@OptIn(InternalSerializationApi::class)
fun GameState.toEpilogueRequest(language: String = "en"): EpilogueRequest {
    val lang = if (language.isBlank()) "en" else language
    val criminalChar = characters.find { it.isCriminal }
    val victimChar = characters.find { it.isVictim }
    val crimeEvent = timeline.getCrimeEvents().firstOrNull()

    val keyClueNames = player.collectedClues.mapNotNull { clueId ->
        getClue(clueId)?.name
    }.take(6)

    return EpilogueRequest(
        case = CaseInfo(
            title = title,
            description = description
        ),
        criminal = CriminalInfo(
            name = criminalChar?.name ?: "Unknown criminal",
            motive = criminalChar?.traits?.firstOrNull() ?: "Motive not provided",
            methodSummary = crimeEvent?.description ?: "Method not specified",
            keyClues = if (keyClueNames.isNotEmpty()) keyClueNames else listOf("No decisive clues collected")
        ),
        victim = VictimInfo(
            name = victimChar?.name ?: "Unknown victim",
            role = victimChar?.traits?.firstOrNull() ?: "Role not specified"
        ),
        player = PlayerInfoEpilogue(
            role = "Detective"
        ),
        language = lang,
        outcome = if (phase == com.ee309.detectivegame.domain.model.GamePhase.WIN) "WIN" else "LOSE"
    )
}
