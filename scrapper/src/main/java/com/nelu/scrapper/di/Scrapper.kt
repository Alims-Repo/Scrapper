package com.nelu.scrapper.di

import com.nelu.scrapper.data.repo.RepoScrapper
import com.nelu.scrapper.data.repo.base.BaseScrapper

class Scrapper {

    companion object {
        private var baseScrapper: BaseScrapper? = null
    }

    private fun inject() {
        baseScrapper = RepoScrapper()
    }

    fun getScrapper() = baseScrapper!!

    init {
        if (baseScrapper == null) inject()
    }
}