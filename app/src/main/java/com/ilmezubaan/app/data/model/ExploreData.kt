package com.ilmezubaan.app.data.model

data class ExploreData(
    val languageName: String,
    val title: String,
    val subtitle: String,
    val heroImage: String,
    val speakers: String,
    val languageFamily: String,
    val history: HistoryData,
    val culture: CultureData,
    val region: RegionData,
    val proverbs: List<InfoCard> = emptyList()
)

data class HistoryData(
    val mainHeading: String,
    val description: String,
    val keyPoints: List<InfoCard>,
    val timeline: List<InfoCard> = emptyList()
)

data class CultureData(
    val mainHeading: String,
    val description: String,
    val images: List<String>,
    val folklore: InfoCard,
    val festivals: List<String> = emptyList()
)

data class RegionData(
    val mainHeading: String,
    val description: String,
    val majorCities: List<String> = emptyList(),
    val dialects: List<String> = emptyList()
)

data class InfoCard(
    val title: String,
    val description: String
)

object ExploreDataProvider {
    val data = mapOf(
        "Punjabi" to ExploreData(
            languageName = "Punjabi",
            title = "The Spirit of the Five Rivers",
            subtitle = "Journey through the vibrant plains and the soulful Sufi traditions of the Punjab.",
            heroImage = "https://images.unsplash.com/photo-1590071210543-98282361665a?q=80&w=600&auto=format&fit=crop",
            speakers = "100 Million+",
            languageFamily = "Indo-Aryan",
            history = HistoryData(
                mainHeading = "Ancient Roots & Sufi Soul",
                description = "Punjabi evolved from Sanskrit through Shauraseni Prakrit. It became a literary giant during the 12th century with Baba Farid, followed by the legendary Sufi era.",
                keyPoints = listOf(
                    InfoCard("Sufi Poetry", "The golden era defined by Bulleh Shah, Waris Shah, and Sultan Bahu."),
                    InfoCard("Indus Civilization", "Home to Harappa, one of the oldest urban centers in human history.")
                ),
                timeline = listOf(
                    InfoCard("12th Century", "Baba Farid Ganjshakar writes the first recorded Punjabi poetry."),
                    InfoCard("1766", "Waris Shah completes the epic 'Heer Ranjha'."),
                    InfoCard("1947", "The partition of Punjab creates a diaspora spread across the globe.")
                )
            ),
            culture = CultureData(
                mainHeading = "Bhangra & Phulkari",
                description = "Punjabi culture is synonymous with hospitality, lively music, and rich agricultural traditions.",
                images = listOf(
                    "https://images.unsplash.com/photo-1566908829550-e6551b00979b?q=80&w=400&auto=format&fit=crop",
                    "https://images.unsplash.com/photo-1583089892943-e02e5b017b6a?q=80&w=400&auto=format&fit=crop"
                ),
                folklore = InfoCard("Heer Ranjha", "The tragic epic of love that defines the romantic folklore of the Punjab."),
                festivals = listOf("Besakhi", "Mela Chiraghan", "Basant")
            ),
            region = RegionData(
                mainHeading = "The Heartland",
                description = "Punjab is the most populous province, characterized by fertile alluvial plains fed by the Indus tributaries.",
                majorCities = listOf("Lahore", "Faisalabad", "Multan", "Gujranwala"),
                dialects = listOf("Majhi (Standard)", "Pothohari", "Doabi", "Jhangvi")
            ),
            proverbs = listOf(
                InfoCard("ڈوہا کتا تے بنیاں تے مت", "A bad companion leads to a bad end."),
                InfoCard("جنہیں لائی گلی، اوہ بھی کدی کدی", "One who tries to deceive often gets caught themselves.")
            )
        ),
        "Sindhi" to ExploreData(
            languageName = "Sindhi",
            title = "The Cradle of Civilization",
            subtitle = "Explore the mystic Sufi shrines and the ancient legacy of the Indus Valley.",
            heroImage = "https://images.unsplash.com/photo-1623851722883-8a30141b714f?q=80&w=600&auto=format&fit=crop",
            speakers = "30 Million+",
            languageFamily = "Indo-Aryan",
            history = HistoryData(
                mainHeading = "Ancient Indus Legacy",
                description = "Sindhi is one of the few languages that has preserved many ancient Prakrit features. It was the first language in which the Quran was translated in the 9th century.",
                keyPoints = listOf(
                    InfoCard("Mohenjo-Daro", "The peak of Bronze Age urban planning in the Indus Valley."),
                    InfoCard("Shah Abdul Latif Bhittai", "The 'Shakespeare of Sindh', whose poetry is the soul of the land.")
                ),
                timeline = listOf(
                    InfoCard("2500 BC", "Flourishing of the Indus Valley Civilization."),
                    InfoCard("712 AD", "Arrival of Muhammad Bin Qasim, introducing Arabic influences."),
                    InfoCard("1853", "The current Sindhi alphabet is standardized by the British.")
                )
            ),
            culture = CultureData(
                mainHeading = "Ajrak & Sufi Raag",
                description = "Sindh is known as the 'Land of Saints' (Bab-ul-Islam), with a culture rooted in peace and tolerance.",
                images = listOf(
                    "https://images.unsplash.com/photo-1543002588-bfa74002ed7e?q=80&w=400&auto=format&fit=crop",
                    "https://images.unsplash.com/photo-1582738411706-bfc8e691d1c2?q=80&w=400&auto=format&fit=crop"
                ),
                folklore = InfoCard("Sassui Punhun", "A tale of devotion and hardship set across the rugged terrains of Sindh and Balochistan."),
                festivals = listOf("Sindh Cultural Day", "Urs of Bhittai", "Lal Shahbaz Qalandar Mela")
            ),
            region = RegionData(
                mainHeading = "The Indus Delta",
                description = "Defined by the Lower Indus basin, ranging from the Thar Desert to the Arabian Sea coast.",
                majorCities = listOf("Karachi", "Hyderabad", "Sukkur", "Larkana"),
                dialects = listOf("Vicholi (Standard)", "Lari", "Thari", "Lasi")
            ),
            proverbs = listOf(
                InfoCard("سچ ته بیڑو پار", "Truth always triumphs."),
                InfoCard("نیکی کر دریا میں وجھ", "Do good and cast it into the river (selfless service).")
            )
        ),
        "Pashto" to ExploreData(
            languageName = "Pashto",
            title = "The Warrior's Path",
            subtitle = "A journey into the rugged mountains and the unbreakable code of Pukhtunwali.",
            heroImage = "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?q=80&w=600&auto=format&fit=crop",
            speakers = "25 Million+ (in Pakistan)",
            languageFamily = "East Iranian",
            history = HistoryData(
                mainHeading = "The Land of Honor",
                description = "Pashto literature took a formal shape in the 16th century with Bayazid Roshan, but reached its zenith with the warrior-poet Khushal Khan Khattak.",
                keyPoints = listOf(
                    InfoCard("Khushal Khan Khattak", "The national poet who fought against the Mughals with sword and pen."),
                    InfoCard("Buddhist Gandhara", "The region was once the global center for Buddhist learning and art.")
                ),
                timeline = listOf(
                    InfoCard("16th Century", "Pata Khazana (The Hidden Treasure) records early Pashto works."),
                    InfoCard("17th Century", "Khushal Khan Khattak unites tribes through poetry."),
                    InfoCard("1947", "KP becomes the gateway between Central Asia and Pakistan.")
                )
            ),
            culture = CultureData(
                mainHeading = "Hospitality & Attan",
                description = "Pukhtun culture is governed by Pukhtunwali: hospitality (Melmastia), revenge (Badal), and asylum (Nanawatai).",
                images = listOf(
                    "https://images.unsplash.com/photo-1524230572899-a752b3835840?q=80&w=400&auto=format&fit=crop",
                    "https://images.unsplash.com/photo-1540959733332-eab4deabeeaf?q=80&w=400&auto=format&fit=crop"
                ),
                folklore = InfoCard("Adam Khan & Durkhanai", "A legendary romantic epic often compared to Romeo and Juliet."),
                festivals = listOf("Jashn-e-Khyber", "Hujra Gatherings", "Poetry Mushairas")
            ),
            region = RegionData(
                mainHeading = "The Northern Valleys",
                description = "A rugged geography of high mountains, including the Hindu Kush range and the fertile Peshawar valley.",
                majorCities = listOf("Peshawar", "Mardan", "Mingora (Swat)", "Abbottabad"),
                dialects = listOf("Yousafzai (Northern)", "Khattak (Southern)", "Wazirwola", "Kandahari")
            ),
            proverbs = listOf(
                InfoCard("چی مینه وی هلته کلا وی", "Where there is love, there is a fort (strength)."),
                InfoCard("خپل عمل د لارې مل", "Your actions are your companion on the path.")
            )
        ),
        "Balochi" to ExploreData(
            languageName = "Balochi",
            title = "The Heart of Balochi Heritage",
            subtitle = "Journey through the rugged terrains and vibrant traditions of the Baloch people.",
            heroImage = "https://images.unsplash.com/photo-1506461883276-594a12b11cf3?q=80&w=600&auto=format&fit=crop",
            speakers = "7 Million+",
            languageFamily = "Northwestern Iranian",
            history = HistoryData(
                mainHeading = "Ancient Origins & Migrations",
                description = "The Baloch people migrated from the Iranian plateau. Their language is closer to Kurdish and Middle Persian than to Indo-Aryan tongues.",
                keyPoints = listOf(
                    InfoCard("Mehrgarh", "Pre-Indus Neolithic site showing 9000 years of history."),
                    InfoCard("Rind-Lashar Wars", "The defining 30-year conflict that shaped Balochi epic poetry.")
                ),
                timeline = listOf(
                    InfoCard("10th Century", "First records of Baloch migrations into the current region."),
                    InfoCard("15th Century", "The era of Chakar Khan Rind, the Great Baloch hero."),
                    InfoCard("19th Century", "Standardization of Balochi grammar and script.")
                )
            ),
            culture = CultureData(
                mainHeading = "Dooch & Chakar's Valor",
                description = "A tribal society where honor and bravery are celebrated through 'Sheyr' (epic ballads).",
                images = listOf(
                    "https://images.unsplash.com/photo-1589118949245-7d38baf380d6?q=80&w=400&auto=format&fit=crop",
                    "https://images.unsplash.com/photo-1516026672322-bc52d61a55d5?q=80&w=400&auto=format&fit=crop"
                ),
                folklore = InfoCard("Hani and Sheh Mureed", "The most popular epic of the Baloch people, symbolizing pure love and chivalry."),
                festivals = listOf("Sibi Mela", "Baloch Culture Day", "Chaukhandi Gatherings")
            ),
            region = RegionData(
                mainHeading = "The Land of Contrast",
                description = "The largest province by area, featuring coastal cliffs, vast deserts, and copper-rich mountains.",
                majorCities = listOf("Quetta", "Gwadar", "Turbat", "Khuzdar"),
                dialects = listOf("Rakhshani", "Makrani", "Sulemani")
            ),
            proverbs = listOf(
                InfoCard("بندگ پہ ہمت اللہ مدد", "Man proposes, God disposes (Effort is man's, help is God's)."),
                InfoCard("راستی رستگ", "Truth brings salvation.")
            )
        ),
        "Saraiki" to ExploreData(
            languageName = "Saraiki",
            title = "The Sweetness of the South",
            subtitle = "Explore the mystical Land of Saints and the ancient heritage of Multan.",
            heroImage = "https://images.unsplash.com/photo-1526481280693-3bfa75ac88b1?q=80&w=600&auto=format&fit=crop",
            speakers = "20 Million+",
            languageFamily = "Indo-Aryan",
            history = HistoryData(
                mainHeading = "The Land of Saints",
                description = "Saraiki is known for its extreme sweetness and musicality. It is the language of the 'Rohi' (Cholistan desert) and the ancient city of Multan.",
                keyPoints = listOf(
                    InfoCard("City of Saints", "Multan, home to hundreds of Sufi shrines and tombs."),
                    InfoCard("Khwaja Ghulam Farid", "The mystic whose poems (Kafis) are considered the peak of Saraiki art.")
                ),
                timeline = listOf(
                    InfoCard("Ancient Era", "Multan thrives as the center of the Sun Temple and later Islamic learning."),
                    InfoCard("19th Century", "Khwaja Farid composes 'Dewan-e-Farid' in the Rohi desert."),
                    InfoCard("Modern Era", "Recognition of Saraiki as a distinct linguistic identity.")
                )
            ),
            culture = CultureData(
                mainHeading = "Blue Pottery & Kashi Kari",
                description = "The culture is a blend of desert resilience and Sufi mysticism, reflected in its art and music.",
                images = listOf(
                    "https://images.unsplash.com/photo-1605000797439-75a150088f44?q=80&w=400&auto=format&fit=crop",
                    "https://images.unsplash.com/photo-1578912914443-e12cff85f695?q=80&w=400&auto=format&fit=crop"
                ),
                folklore = InfoCard("Sohni Mahiwal", "A tragic tale of love that echoed across the Indus and Chenab rivers."),
                festivals = listOf("Urs of Shah Rukn-e-Alam", "Sangat Festivals", "Basant in the South")
            ),
            region = RegionData(
                mainHeading = "The Rohi Desert",
                description = "The Saraiki belt links Punjab, Sindh, and Balochistan, centered around the confluence of Pakistan's major rivers.",
                majorCities = listOf("Multan", "Bahawalpur", "Dera Ghazi Khan", "Bhakkar"),
                dialects = listOf("Multani (Standard)", "Riasti", "Thali", "Derewali")
            ),
            proverbs = listOf(
                InfoCard("نیکی کر، تے دریا سٹ", "Do good and cast it into the river."),
                InfoCard("ہمت مرداں، مدد خدا", "Courage of men, help from God.")
            )
        ),
        "Urdu" to ExploreData(
            languageName = "Urdu",
            title = "The National Connect",
            subtitle = "A sophisticated blend of cultures that became the national voice of Pakistan.",
            heroImage = "https://images.unsplash.com/photo-1546410531-bb4caa6b424d?q=80&w=600&auto=format&fit=crop",
            speakers = "100 Million+ (Globally)",
            languageFamily = "Indo-Aryan",
            history = HistoryData(
                mainHeading = "Lashkari Zaban",
                description = "Urdu emerged from the interactions of Persian, Arabic, and Turkic speakers with the local Khari Boli. It became the language of high culture and independence.",
                keyPoints = listOf(
                    InfoCard("Poetic Peak", "The era of Ghalib and Iqbal, who used Urdu to awaken the Muslim consciousness."),
                    InfoCard("National Identity", "Adopted as the national language of Pakistan to unite its diverse provinces.")
                ),
                timeline = listOf(
                    InfoCard("13th Century", "Amir Khusro begins experimenting with early Urdu forms."),
                    InfoCard("1860s", "The Aligarh Movement promotes Urdu for educational reform."),
                    InfoCard("1948", "Quaid-e-Azam declares Urdu as the national language.")
                )
            ),
            culture = CultureData(
                mainHeading = "Adab & Mushaira",
                description = "Urdu is defined by 'Adab' (etiquette). The Mushaira is a unique cultural gathering where poets recite to large audiences.",
                images = listOf(
                    "https://images.unsplash.com/photo-1561089489-f13d5e730d72?q=80&w=400&auto=format&fit=crop",
                    "https://images.unsplash.com/photo-1501504905252-473c47e087f8?q=80&w=400&auto=format&fit=crop"
                ),
                folklore = InfoCard("Dastan-e-Amir Hamza", "One of the world's longest cycles of magical adventure tales."),
                festivals = listOf("Iqbal Day", "Ghalib Centenary", "Annual Literary Festivals")
            ),
            region = RegionData(
                mainHeading = "The Common Link",
                description = "While rooted in urban centers, Urdu is the lingua franca understood from the Karakoram to the Arabian Sea.",
                majorCities = listOf("Islamabad", "Karachi", "Lahore", "Hyderabad"),
                dialects = listOf("Deccani", "Dahlvi", "Modern Standard")
            ),
            proverbs = listOf(
                InfoCard("نیکی کر، دریا میں ڈال", "Do good and cast it into the river."),
                InfoCard("جہاں چاہ، وہاں راہ", "Where there's a will, there's a way.")
            )
        )
    )
}
