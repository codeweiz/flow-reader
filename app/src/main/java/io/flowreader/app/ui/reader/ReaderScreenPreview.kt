package io.flowreader.app.ui.reader

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import io.flowreader.domain.model.Chapter

@Preview(showBackground = true, device = "id:pixel_7")
@Composable
private fun ReaderScreenPreview() {
    val fakeChapters = listOf(
        Chapter(
            bookId = "1",
            index = 0,
            title = "第一章 退婚",
            startCharIndex = 0,
            endCharIndex = 1200,
            content = "青州，陆家。\n\n如今这个局面，你们陆家退婚也是唯一的出路。来人淡淡开口。\n\n主位上，陆家主母柳妇人脸色有些难看，却也没有再多说什么。\n\n“退婚可以。”\n\n坐在下首的少女穿着一身素青衣裹，面容英俊而淡漠，看上去不过十五六岁，身上有着和年龄不符的冷静。\n\n“但有一个条件。”\n\n少女抬头望向柳妇人，声音清冷：“陆家要给我一纸修书信，内容由我来写。”"
        ),
        Chapter(
            bookId = "1",
            index = 1,
            title = "第二章 修书",
            startCharIndex = 1200,
            endCharIndex = 2400,
            content = "修书信？柳妇人气笑了。\n\n你一个小小的边城小孪，也配让我们陆家给你修书信？陆家有人嘲笑道。\n\n少女面不改色，只是静静地看着柳妇人。\n\n柳妇人的脸色沉了下来。\n\n好，陆家给你修书信。但从今往后，你与陆家再无任何关系！"
        )
    )
    ReaderContent(
        title = "我的退婚大佬",
        chapters = fakeChapters,
        currentIndex = 0,
        onPrev = {},
        onNext = {}
    )
}
