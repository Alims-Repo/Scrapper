package com.nelu.scrapper.di

import com.nelu.scrapper.data.repo.RepoScrapper
import com.nelu.scrapper.data.repo.base.BaseScrapper

class Scrapper {
    companion object {
        private lateinit var baseScrapper: BaseScrapper
    }

    private fun inject() {
        baseScrapper = RepoScrapper()
    }

    fun getScrapper() = baseScrapper

    init { inject() }
}