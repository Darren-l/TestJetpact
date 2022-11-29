package cn.gd.snm.testjetpact.development.mvvm


data class DecsEntity(
    var cp_source: String = "",
    var detail: Detail = Detail(),
    val id: Int = 0,
    val obj_type: String = "",
    val error:String = ""
)

data class Detail(
    val area_name: String="",
    val category: String="",
    val cid: String="",
    val code: String="",
    val copyright: String="",
    val desc: String="",
    val director: String="",
    val eposide_all: String="",
    val eposide_at: String="",
    val hz_pic: String="",
    val leadingactor: String="",
    val mark_pic: String="",
    val pay_status: Int=0,
    val rec_type: Int=0,
    val relate_code: List<RelateCode> = arrayListOf(),
    val score: String="",
    val sec_title: String="",
    val sub_genre: String="",
    var title: String="",
    val type: Int=0,
    val vid: String="",
    val vt_pic: String="",
    val year: String=""
)

data class RelateCode(
    val relate_name: String="",
    val relate_type: String="",
    val req_contenttype: String="",
    val req_videotype: String=""
)