#!/usr/bin/python

from bs4 import Tag
from models import MangaChapter
from models import MangaPage
from models import MangaSeries

class MangaPandaSeriesScraper:
	def get_manga_list(self, soup):
		series = []

		content_div = soup.find(class_='content_bloc2')
		series_cols = content_div.find_all('div', class_='series_col')
		for series_col in series_cols:
			series_alphas = series_col.find_all('div', class_='series_alpha')
			for series_alpha in series_alphas:
				list_items = series_alpha.ul.find_all('li')
				for li in list_items:
					a = li.a
					name = a.text.strip()
					url = a.get('href').strip()

					series.append(MangaSeries(name, url))

		return series


class MangaPandaChapterScraper:

	def get_series_summary(self, soup):
		summary_div = soup.find(id='readmangasum')
		return summary_div.find('p').text

	def get_series_cover_image_url(self, soup):
		img_div = soup.find(id='mangaimg')
		return img_div.find('img').get('src')

	def get_series_year_of_release(self, soup):
		rows = self.__get_properties_rows(soup)
		return rows[2].find_all('td')[1].text

	def get_series_author(self, soup):
		rows = self.__get_properties_rows(soup)
		return rows[4].find_all('td')[1].text

	def get_series_artist(self, soup):
		rows = self.__get_properties_rows(soup)
		return rows[5].find_all('td')[1].text

	def get_series_genre_list(self, soup):
		rows = self.__get_properties_rows(soup)
		genres = rows[7].find_all('td')[1].find_all('a')
		if genres is not None:
			retval = []
			for g in genres:
				if g.text is not None:
					retval.append(g.text)
			return retval

	def __get_properties_rows(self, soup):
		div = soup.find(id='mangaproperties')
		return div.find('table').find_all('tr')

	def get_chapter_urls(self, soup):
		chapters = []

		chapter_list = soup.select('#chapterlist')
		for tag in chapter_list:
			for item in tag.table.children:
				if isinstance(item, Tag):
					tr = item.select('td')
					if len(tr) == 2:
						link_elem = tr[0].select('a')[0]
						date_elem = tr[1]

						name = link_elem.text.strip()
						url = link_elem.get('href').strip()
						date = date_elem.text.strip()

						chapters.append(MangaChapter(name, url, date))

		return chapters


class MangaPandaPageScraper:
	def get_pages_url(self, soup):
		pages = []

		page_menu = soup.find(id='pageMenu')
		option_items = page_menu.find_all('option')
		for option in option_items:
			name = option.text
			url = option.get('value')

			pages.append(MangaPage(name, url))

		return pages

	def get_image_url(self, soup):
		return soup.find(id="img").get('src')



