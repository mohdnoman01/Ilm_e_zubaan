package com.ilmezubaan.app.data.util

import com.google.gson.Gson
import com.ilmezubaan.app.data.local.entities.ConceptEntity
import com.ilmezubaan.app.data.local.entities.ConceptLanguageData
import com.ilmezubaan.app.ui.viewmodel.ConceptViewModel

object DataImporter {
    private val gson = Gson()

    fun importPunjabiData(viewModel: ConceptViewModel) {
        val rawData = """
            1	میں	میں	I	Basic
            2	تو	تم	You	Basic
            3	اوہ	وہ	He/She	Basic
            4	اسی	ہم	We	Basic
            5	تسی	آپ لوگ	You (plural)	Basic
            6	اوہناں	وہ لوگ	They	Basic
            7	پانی	پانی	Water	Basic
            8	روٹی	روٹی	Bread	Basic
            9	دودھ	دودھ	Milk	Basic
            10	چاول	چاول	Rice	Basic
            11	گھر	گھر	House	Basic
            12	دروازہ	دروازہ	Door	Basic
            13	کتاب	کتاب	Book	Basic
            14	قلم	قلم	Pen	Basic
            15	مدرسہ	مدرسہ	School	Basic
            16	بازار	بازار	Market	Basic
            17	دوست	دوست	Friend	Basic
            18	بھرا	بھائی	Brother	Basic
            19	پیو	باپ	Father	Basic
            20	ماں	ماں	Mother	Basic
            21	بیٹی	بیٹی	Daughter	Basic
            22	پتر	بیٹا	Son	Basic
            23	کھانا	کھانا	Food	Basic
            24	سونا	سونا	Sleep	Basic
            25	چلنا	چلنا	Walk	Basic
            26	بولنا	بولنا	Speak	Basic
            27	سننا	سننا	Listen	Basic
            28	ویکھنا	دیکھنا	See	Basic
            29	کرنا	کرنا	Do	Basic
            30	لکھنا	لکھنا	Write	Basic
            31	پڑھنا	پڑھنا	Read	Basic
            32	آنا	آنا	Come	Basic
            33	جانا	جانا	Go	Basic
            34	بیٹھنا	بیٹھنا	Sit	Basic
            35	کھڑنا	کھڑا ہونا	Stand	Basic
            36	اچھا	اچھا	Good	Basic
            37	برا	برا	Bad	Basic
            38	چھوٹا	چھوٹا	Small	Basic
            39	وڈا	بڑا	Big	Basic
            40	گرم	گرم	Hot	Basic
            41	ٹھنڈا	ٹھنڈا	Cold	Basic
            42	دن	دن	Day	Basic
            43	رات	رات	Night	Basic
            44	وقت	وقت	Time	Basic
            45	کام	کام	Work	Basic
            46	پیسہ	پیسہ	Money	Basic
            47	دل	دل	Heart	Basic
            48	ہاتھ	ہاتھ	Hand	Basic
            49	پیر	پاؤں	Foot	Basic
            50	آنکھ	آنکھ	Eye	Basic
            51	ناک	ناک	Nose	Basic
            52	کان	کان	Ear	Basic
            53	منہ	منہ	Mouth	Basic
            54	سر	سر	Head	Basic
            55	زمین	زمین	Land	Basic
            56	آسمان	آسمان	Sky	Basic
            57	سورج	سورج	Sun	Basic
            58	چاند	چاند	Moon	Basic
            59	ستارہ	ستارہ	Star	Basic
            60	ہاں	ہاں	Yes	Basic
            61	نہیں	نہیں	No	Basic
            62	زندگی	زندگی	Life	Basic
            63	محبت	محبت	Love	Basic
            64	نفرت	نفرت	Hate	Basic
            65	خوشی	خوشی	Happiness	Basic
            66	غم	غم	Sorrow	Basic
            67	امید	امید	Hope	Basic
            68	خواب	خواب	Dream	Basic
            69	سچ	سچ	Truth	Basic
            70	جھوٹ	جھوٹ	Lie	Basic
            71	حق	حق	Right	Basic
            72	عدل	انصاف	Justice	Basic
            73	علم	علم	Knowledge	Basic
            74	عقل	عقل	Wisdom	Basic
            75	تجربہ	تجربہ	Experience	Basic
            76	سفر	سفر	Journey	Basic
            77	کامیابی	کامیابی	Success	Basic
            78	ناکامی	ناکامی	Failure	Basic
            79	قوت	طاقت	Strength	Basic
            80	ضعف	کمزوری	Weakness	Basic
            81	دوستی	دوستی	Friendship	Basic
            82	دشمنی	دشمنی	Enmity	Basic
            83	عبادت	عبادت	Worship	Basic
            84	نماز	نماز	Prayer	Basic
            85	روزہ	روزہ	Fast	Basic
            86	صدقہ	صدقہ	Charity	Basic
            87	صبر	صبر	Patience	Basic
            88	شکر	شکر	Gratitude	Basic
            89	زندگی	زندگی	Life	Basic
            90	محبت	محبت	Love	Basic
            91	نفرت	نفرت	Hate	Basic
            92	خوشی	خوشی	Happiness	Basic
            93	غم	غم	Sorrow	Basic
            94	امید	امید	Hope	Basic
            95	خواب	خواب	Dream	Basic
            96	سچ	سچ	Truth	Basic
            97	جھوٹ	جھوٹ	Lie	Basic
            98	حق	حق	Right	Basic
            99	عدل	انصاف	Justice	Basic
            100	علم	علم	Knowledge	Basic
        """.trimIndent()

        val lines = rawData.lines()
        val concepts = lines.mapNotNull { line ->
            val parts = line.split("\t")
            if (parts.size >= 5) {
                val languages = mapOf(
                    "punjabi" to ConceptLanguageData(
                        script = parts[1],
                        roman = parts[3],
                        audioUrl = null
                    ),
                    "urdu" to ConceptLanguageData(
                        script = parts[2],
                        roman = "",
                        audioUrl = null
                    )
                )
                
                ConceptEntity(
                    conceptId = "concept_${parts[0]}",
                    englishMeaning = parts[3],
                    category = parts[4],
                    difficultyLevel = "1",
                    languagesJson = gson.toJson(languages),
                    updatedAt = System.currentTimeMillis()
                )
            } else null
        }
        
        viewModel.insertConcepts(concepts)
    }
}
