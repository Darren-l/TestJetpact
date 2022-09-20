package cn.gd.snm.testjetpact.development.mvvm

data class ListInfoEntity(
    val datas: List<Data>,
    val retcode: String,
    val retmsg: String,
    val total: Int
)

data class Data(
    val cid: String,
    val code: String,
    val contentidx: Int,
    val contenttype: Int,
    val coverfree: String,
    val covertitle: String,
    val covertype: Int,
    val cp_source: String,
    val duration: String,
    val episodeno: String,
    val eposide: String,
    val head_time: Int,
    val mark_pic: String,
    val next_title: String,
    val next_vid: String,
    val oseq: Int,
    val periods: String,
    val pic_url: String,
    val pp_id: String,
    val score: String,
    val sec_title: String,
    val tail_time: Int,
    val title: String,
    val vid: String,
    val videotype: String,
    val vpay_status: Int
)