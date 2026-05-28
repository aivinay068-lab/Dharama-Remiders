package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.FestivalEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [FestivalEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun festivalDao(): FestivalDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "utsav_festival_db"
                )
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        // Pre-populate data on database creation
                        CoroutineScope(Dispatchers.IO).launch {
                            val dao = getInstance(context).festivalDao()
                            dao.insertFestivals(getSeedFestivals())
                        }
                    }
                })
                .build()
                INSTANCE = instance
                instance
            }
        }

        private fun getINSTANCE(): AppDatabase {
            return INSTANCE!!
        }

        fun getSeedFestivals(): List<FestivalEntity> {
            return listOf(
                FestivalEntity(
                    name = "Nirjala Ekadashi",
                    category = "Vrat",
                    date = "2026-06-26",
                    tithi = "Jyeshtha Shukla Ekadashi",
                    deity = "Lord Vishnu",
                    significance = "One of the most sacred and rigorous Ekadashis. Nirjala means 'without water'. Keeping this fast is believed to grant the virtues of all 24 Ekadashis in the year.",
                    whatToDo = "Observe a complete waterless fast from sunrise to the next day's sunrise. Visit Vishnu/Krishna temples. Dedicate the day to charity, donating clothing and earthen pots of cool water to dry/needy people.",
                    mantras = "Om Namo Bhagavate Vasudevaya",
                    fastingRules = "Strictly waterless (Nirjala). For children, elderly, or those with medical issues, a light fruit and milk diet (Phalahar) is allowed.",
                    isFastingDay = true
                ),
                FestivalEntity(
                    name = "Pradosh Vrat",
                    category = "Vrat",
                    date = "2026-06-12",
                    tithi = "Jyeshtha Krishna Trayodashi",
                    deity = "Lord Shiva",
                    significance = "Pradosh Vrat is observed twice every month during twilight. It is meant to obtain the blessings of Lord Shiva and Goddess Parvati and to dissolve negative karmas.",
                    whatToDo = "Perform a twilight puja (Pradosh Kaal) between sunset and dusk. Cleanse the home, light a ghee lamp, offer Bilva leaves, water, and milk to Shiva Lingam. Recite Shiva Chalisa.",
                    mantras = "Om Namah Shivaya",
                    fastingRules = "Observe partial dry fast or eat plain fruits and milk during the daytime. Break the fast after evening puja with Satvik food.",
                    isFastingDay = true
                ),
                FestivalEntity(
                    name = "Maha Shivratri (Special Remembrances)",
                    category = "Festival",
                    date = "2026-02-15",
                    tithi = "Phalguna Krishna Chaturdashi",
                    deity = "Lord Shiva",
                    significance = "The Great Night of Shiva, celebrating the cosmic union of Shiva and Parvati. Symbolizes winning over darkness and ignorance.",
                    whatToDo = "Observe a full-day fast. Keep a night vigil (Jagran) and participate in Rudrabhishek (bathing Shiva Lingam with 5 holy substances: milk, honey, curd, ghee, sugar). Sing Shiva Bhajans.",
                    mantras = "Maha Mrityunjaya Mantra: Om Tryambakam Yajamahe Sugandhim Pushti-Vardhanam...",
                    fastingRules = "Strict waterless fast or Phalahari (fruits, nuts, milk-products, buckwheat/Kuttu flour). No onion, garlic, or standard grains.",
                    isFastingDay = true
                ),
                FestivalEntity(
                    name = "Devshayani Ekadashi",
                    category = "Vrat",
                    date = "2026-07-25",
                    tithi = "Ashadha Shukla Ekadashi",
                    deity = "Lord Vishnu",
                    significance = "Marks the beginning of Chaturmas, a holy four-month period. It is believed Lord Vishnu goes into yogic sleep (Yoganidra) on the cosmic serpent Shesha on this day.",
                    whatToDo = "Observe sacred fasting. Take holy bath, offer yellow flowers, milk, and sweets to Lord Vishnu. Begin a four-month spiritual vow of self-discipline (Chaturmas Vow).",
                    mantras = "Om Vishnave Namah",
                    fastingRules = "Fasting starting from sunrise to next day's sunrise. Grains, beans, and certain spices are forbidden. Phalahar is allowed.",
                    isFastingDay = true
                ),
                FestivalEntity(
                    name = "Hariyali Teej",
                    category = "Festival",
                    date = "2026-08-15",
                    tithi = "Shravana Shukla Tritiya",
                    deity = "Goddess Parvati & Lord Shiva",
                    significance = "Celebrates the elements of nature, greenery, and monsoon. Symbolizes the reunion of Goddess Parvati and Lord Shiva.",
                    whatToDo = "Women dress in green attire, apply Mehendi (henna) on hands, and enjoy swings. Gather for Teej puja, listen to the Teej Vrat Katha, and seek conjugal bliss.",
                    mantras = "Om Umamaheshwarabhyam Namah",
                    fastingRules = "Many women observe a waterless fast (Nirjala) for prosperity of husband and family.",
                    isFastingDay = true
                ),
                FestivalEntity(
                    name = "Raksha Bandhan",
                    category = "Special Day",
                    date = "2026-08-28",
                    tithi = "Shravana Purnima",
                    deity = "None",
                    significance = "An ancient Indian festival celebrating the sacred bond of love and protection between brothers and sisters.",
                    whatToDo = "Sisters tie a protective sacred thread (Rakhi) around brothers' wrists, apply tilak on forehead, and perform aarti. Brothers give gifts and pledge lifetime protection to sisters.",
                    mantras = "Yena baddho bali raja danavendro mahabalah...",
                    fastingRules = "Sisters usually fast in the morning until they tie the Rakhi.",
                    isFastingDay = false
                ),
                FestivalEntity(
                    name = "Krishna Janmashtami",
                    category = "Festival",
                    date = "2026-09-03",
                    tithi = "Bhadrapada Krishna Ashtami",
                    deity = "Lord Krishna",
                    significance = "Celebrates the earthly birth (incarnation) of Lord Krishna, the eighth avatar of Lord Vishnu, Born to destroy evil.",
                    whatToDo = "Observe fast until Lord Krishna's birth at midnight. Set up a cradle with baby Krishna (Ladoo Gopal) idol. Offer 56 food delicacies (Chappan Bhog), rock the cradle, blow conch, sing praises.",
                    mantras = "Hare Krishna Hare Krishna Krishna Krishna Hare Hare, Hare Rama Hare Rama Rama Rama Hare Hare",
                    fastingRules = "Phalahari fast during the day. Water, milk, and fresh fruit juices are taken. Break fast after midnight rituals with Satvik prasad.",
                    isFastingDay = true
                ),
                FestivalEntity(
                    name = "Ganesh Chaturthi",
                    category = "Festival",
                    date = "2026-09-15",
                    tithi = "Bhadrapada Shukla Chaturthi",
                    deity = "Lord Ganesha",
                    significance = "Marks the arrival of Lord Ganesha, the god of wisdom, prosperity, and obstacle removal, from Kailash to Earth.",
                    whatToDo = "Install clay idols of Ganesha at homes (Ganesh Sthapana). Offer red flowers, Durva grass, and fresh Modaks (traditional sweet). Perform morning and evening aartis.",
                    mantras = "Om Gan Ganapataye Namah",
                    fastingRules = "Simple fasting index. Avoid heavy grains. Feast on satvik items like modaks, coconut laddoos, and sabudana.",
                    isFastingDay = false
                ),
                FestivalEntity(
                    name = "Sharad Navratri Begin",
                    category = "Festival",
                    date = "2026-10-11",
                    tithi = "Ashvin Shukla Pratipada",
                    deity = "Goddess Durga",
                    significance = "The auspicious nine nights dedicated to Goddess Durga and her nine sacred forms (Navadurga), representing divine feminine energy and protection.",
                    whatToDo = "Perform 'Ghatasthapana' (sowing seeds in clay pot). Light a constant flame (Akhand Jyoti). Perform Durga Puja every morning and evening. Dance Garba/Dandiya in groups.",
                    mantras = "Om Dum Durgayei Namaha",
                    fastingRules = "Eat only Satvik diets. Strict avoidance of wheat, rice, pulses, onion, garlic, or non-veg. Use Singhare ka atta (chestnut flour) and Rock salt (sendha namak).",
                    isFastingDay = true
                ),
                FestivalEntity(
                    name = "Dussehra (Vijayadashami)",
                    category = "Special Day",
                    date = "2026-10-20",
                    tithi = "Ashvin Shukla Dashami",
                    deity = "Lord Rama & Goddess Durga",
                    significance = "Celebrates Lord Rama's victory over the multi-headed demon king Ravana, and Goddess Durga's victory over Mahishasura. Symbolizes victory of good over evil.",
                    whatToDo = "Participate in Ramlila events. Burn effigies of Ravana, Kumbhakarna, and Meghnada. Touch feet of elders for blessings. Distribute Shami leaves as gold representation.",
                    mantras = "Sri Rama Rama Rameti Rame Rame Manorame...",
                    fastingRules = "No fasting required. It is a day of feast and celebrations.",
                    isFastingDay = false
                ),
                FestivalEntity(
                    name = "Karwa Chauth",
                    category = "Vrat",
                    date = "2026-10-28",
                    tithi = "Kartika Krishna Chaturthi",
                    deity = "Goddess Parvati",
                    significance = "Observed by married Hindu women seeking safety, longevity, and well-being of their husbands.",
                    whatToDo = "Eat pre-dawn meal (Sargi). Keep rigid waterless fast (Nirjala) from sunrise. Draw Gaura Mata icon, listen to the Karwa Chauth Katha in the afternoon. Break fast at moonrise by viewing moon through sieve and sipping liquid from husband's hand.",
                    mantras = "Om Chanyai Namah (Prayer to Goddess Parvati)",
                    fastingRules = "Rigid Nirjala fast (absolutely no food or water) from sunrise until moon is sighted.",
                    isFastingDay = true
                ),
                FestivalEntity(
                    name = "Karwa Chauth Passed (Legacy)",
                    category = "Vrat",
                    date = "2025-10-28",
                    tithi = "Kartika Krishna Chaturthi",
                    deity = "Goddess Parvati",
                    significance = "Historical representation of holy fasts passed.",
                    whatToDo = "Observe and look back at traditional fasts.",
                    mantras = "Om Chanyai Namah",
                    fastingRules = "Strict fast",
                    isFastingDay = true
                ),
                FestivalEntity(
                    name = "Dhanteras",
                    category = "Special Day",
                    date = "2026-11-07",
                    tithi = "Kartika Krishna Trayodashi",
                    deity = "Goddess Lakshmi & Lord Dhanvantari",
                    significance = "First day of Diwali, commemorating the birth of Dhanvantari (God of Ayurvedic health) and the emergence of Goddess Lakshmi from ocean churning.",
                    whatToDo = "Cleanse the house. Buy metal utensils, gold or silver coins, or brass decorative items. In the evening, light a Yamachari ghee lamp (facing South) to ward off untimely demise.",
                    mantras = "Om Shreem Hreem Shreem Kamale Kamalalaye Praseed Praseed...",
                    fastingRules = "No specific fasting required.",
                    isFastingDay = false
                ),
                FestivalEntity(
                    name = "Diwali (Kartika Amavasya)",
                    category = "Festival",
                    date = "2026-11-09",
                    tithi = "Kartika Amavasya",
                    deity = "Goddess Lakshmi & Lord Rama",
                    significance = "The major Indian festival of lights. Celebrates Lord Rama's return to Ayodhya after 14 years of exile and welcome of Goddess Lakshmi for wealth and prosperity.",
                    whatToDo = "Decorate the house doorways with colourful powder rangoli patterns and lights. Conduct family Lakshmi Puja in the evening placing gold ornaments, coins, and sweets. Light clay oil diyas around the house.",
                    mantras = "Om Sri Maha Lakshmyai Namah",
                    fastingRules = "No grain restrictions during festivities, but eating strict clean vegetarian, satvik meals is mandatory.",
                    isFastingDay = false
                ),
                FestivalEntity(
                    name = "Devutthana Ekadashi (Tulsi Vivah)",
                    category = "Vrat",
                    date = "2026-11-20",
                    tithi = "Kartika Shukla Ekadashi",
                    deity = "Lord Vishnu",
                    significance = "Believed to be the day Lord Vishnu wakes up from his 4-month yogic sleep. Ceremonial wedding of Tulsi herb to Shaligram (Vishnu form) is performed.",
                    whatToDo = "Keep Vishnu fast. Set up sugarcane canopy, bathe Tulsi plant, offer bridal ornaments, and perform the wedding of Tulsi and Shaligram. Recite prayers to start the auspicious wedding season.",
                    mantras = "Om Namo Bhagavate Vasudevaya",
                    fastingRules = "Nirjala fast or Phalahar. Many observe partial fast breaking it after organizing Tulsi Marriage in evening.",
                    isFastingDay = true
                ),
                // --- MULTI-RELIGION FESTIVAL & VRAT ENTRIES ---
                FestivalEntity(
                    name = "Eid al-Adha (Bakrid)",
                    category = "Festival",
                    date = "2026-05-27",
                    tithi = "Islamic Calendar (10 Dhu al-Hijjah)",
                    deity = "Allah (Islam)",
                    significance = "Commemorates Ibrahim's absolute devotion tool when requested to sacrifice his son in obedience. A beautiful reminder of family love and spiritual submission.",
                    whatToDo = "Perform morning congregation prayers, greet 'Eid Mubarak', share wealth via charity (Zakat), cook festive meals, and distribute them to relatives and the needy.",
                    mantras = "Takbeerat: Allahu Akbar, Allahu Akbar, La ilaha illa Allah, Allahu Akbar, Wa lillahil hamd",
                    fastingRules = "Fasting is prohibited on Eid days (specifically structured around sharing a communal feast with loved ones).",
                    isFastingDay = false
                ),
                FestivalEntity(
                    name = "Eid al-Fitr (Ramadan Feast)",
                    category = "Festival",
                    date = "2026-03-20",
                    tithi = "Islamic Calendar (1 Shawwal)",
                    deity = "Allah (Islam)",
                    significance = "The joyous 'Festival of Breaking the Fast', marking the holy culmination of the 30-day dawn-to-sunset fasting of Ramadan.",
                    whatToDo = "Offer special Eid prayers, pay 'Zakat al-Fitr' (charity to the poor), host family gatherings, wear clean or new clothes, and prepare sweets.",
                    mantras = "Eid Takbeer for praise and thanksgiving",
                    fastingRules = "Fasting is prohibited on Eid day; celebrating completion of Ramadan.",
                    isFastingDay = false
                ),
                FestivalEntity(
                    name = "Christmas Day",
                    category = "Festival",
                    date = "2026-12-25",
                    tithi = "Christian Calendar (December 25)",
                    deity = "Jesus Christ (Christianity)",
                    significance = "Annual festival celebrating the birth of Jesus Christ, the central figure and divine savior in Christianity.",
                    whatToDo = "Attend midnight church mass, decorate house with festive lights and Christmas tree, sing praises/carols, and share gifts.",
                    mantras = "Prayer of Grace: Praise and Glory to God in the highest, and peace to his people on earth.",
                    fastingRules = "Traditional winter feast, no general fasting required on the main day.",
                    isFastingDay = false
                ),
                FestivalEntity(
                    name = "Good Friday",
                    category = "Special Day",
                    date = "2026-04-03",
                    tithi = "Holy Week / Good Friday",
                    deity = "Jesus Christ (Christianity)",
                    significance = "Solemn day commemorating the holy crucifixion and sacrificial death of Jesus Christ on the cross at Calvary.",
                    whatToDo = "Attend deep church services, fast, pray, contemplate on mercy, love, and redemption. Maintain peaceful, quiet meditation.",
                    mantras = "The Lord's Prayer: Our Father in heaven, hallowed be your name...",
                    fastingRules = "Traditional self-denial and fast. Restricting dairy, meat, and consuming only a single light vegetarian meal with water.",
                    isFastingDay = true
                ),
                FestivalEntity(
                    name = "Guru Nanak Gurpurab",
                    category = "Festival",
                    date = "2026-11-24",
                    tithi = "Sikh Calendar (Karthik Purnima)",
                    deity = "Guru Nanak Dev Ji (Sikhism)",
                    significance = "Birth anniversary of Guru Nanak Dev Ji, the first Sikh Guru and cosmic messenger of absolute oneness and truth.",
                    whatToDo = "Listen to Akhand Path (full continuous reading of Guru Granth Sahib), participate in Prabhat Pheris (early morning praises/songs), and serve in glorious Langar kitchens.",
                    mantras = "Mool Mantar: Ik Onkar Satnam Karta Purakh Nirbhau Nirvair Akal Murat Ajuni Saibhang Gur Prasad...",
                    fastingRules = "Sikh dharma explicitly discourages self-mortification via fasting; participate in community Langar.",
                    isFastingDay = false
                ),
                FestivalEntity(
                    name = "Mahavir Jayanti",
                    category = "Festival",
                    date = "2026-03-31",
                    tithi = "Jain Calendar (Chaitra Shukla 13)",
                    deity = "Lord Mahavira (Jainism)",
                    significance = "Celebrates the birth anniversary of Lord Mahavira, the 24th and last Tirthankara, champion of absolute Ahimsa (non-violence) and peace.",
                    whatToDo = "Bathe Mahavira idols, conduct spiritual prayers/sermons, donate to cow shelters, clean temples, and engage in meditative vows of absolute non-injury.",
                    mantras = "Navkar Mantra: Namo Arihantanam, Namo Siddhanam, Namo Ayariyanam...",
                    fastingRules = "Strict vegetarianism (Jain Satvik). Many devout Jains observe a rigorous, silent complete fast (Upvas) denying all solid food.",
                    isFastingDay = true
                ),
                FestivalEntity(
                    name = "Buddha Purnima (Vesak)",
                    category = "Festival",
                    date = "2026-05-31",
                    tithi = "Vaisakha Purnima",
                    deity = "Gautama Buddha (Buddhism)",
                    significance = "Trifold sacred day celebrating Gautama Buddha's birth, absolute enlightenment (Nirvana), and final passage to peace (Parinirvana).",
                    whatToDo = "Visit sacred Buddhist temples, illuminate butter lamps, practice silence & Vipassana meditation, wear plain white robes, and perform acts of immense charity.",
                    mantras = "Refuge Chants: Buddham Saranam Gacchami, Dhammam Saranam Gacchami, Sangham Saranam Gacchami",
                    fastingRules = "Strict Satvik vegetarian food. Many avoid solid food after midday as a discipline of lay practitioners.",
                    isFastingDay = true
                )
            )
        }
    }
}
