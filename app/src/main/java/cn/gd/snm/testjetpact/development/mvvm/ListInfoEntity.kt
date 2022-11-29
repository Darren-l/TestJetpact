package cn.gd.snm.testjetpact.development.mvvm

/**
 * recyclerView的刷新状态及方式可以定义在数据中
 *
 */
data class ListInfoEntity(
    val datas: List<Data> = arrayListOf(),
    val retcode: String="",
    val retmsg: String="",
    val total: Int=0
){
    //刷新局部还是全部
    var notifyAll = true
    //当前需要刷新的位置
    var notifyPos = 0

    //当前选中位置
    var selectPos = -1
    var lastSelectPos = -1

}

data class Data(
    val cid: String="",
    val code: String="",
    val contentidx: Int=0,
    val contenttype: Int=0,
    val coverfree: String="",
    val covertitle: String="",
    val covertype: Int=0,
    val cp_source: String="",
    val duration: String="",
    val episodeno: String="",
    val eposide: String="",
    val head_time: Int=0,
    val mark_pic: String="",
    val next_title: String="",
    val next_vid: String="",
    val oseq: Int=0,
    val periods: String="",
    val pic_url: String="",
    val pp_id: String="",
    val score: String="",
    val sec_title: String="",
    val tail_time: Int=0,
    val title: String="",
    val vid: String="",
    val videotype: String="",
    val vpay_status: Int=0
){
    //当前item是否被选中
    var isSelect = false

}