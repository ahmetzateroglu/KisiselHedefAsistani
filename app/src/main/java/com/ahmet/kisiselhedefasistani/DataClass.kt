package com.ahmet.kisiselhedefasistani


class DataClass {

    var ethedefismi: String? =null
    var ethedefoncelik: String? =null
    var ethedefkategori: String? =null
    var ethedefaciklama: String? =null
    var iwhedefresim: String? =null
    var key: String? = null


    constructor(ethedefismi:String?,ethedefoncelik:String?,ethedefkategori:String?,ethedefaciklama:String?,iwhedefresim:String?){
        this.ethedefismi=ethedefismi
        this.ethedefoncelik=ethedefoncelik
        this.ethedefkategori=ethedefkategori
        this.ethedefaciklama=ethedefaciklama
        this.iwhedefresim=iwhedefresim
    }
    constructor(){

    }
}