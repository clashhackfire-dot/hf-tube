/*
 * Injects a small download icon next to each video card's metadata line
 * (channel/views/date text), right-aligned, not on the thumbnail. Tapping
 * it calls HFTube.onDownloadClicked(videoUrl) via the JS interface.
 *
 * FRAGILE BY DESIGN: this targets YouTube's mobile-web DOM, which changes
 * without notice. The selector below (a[href*="/watch?v="] whose closest
 * ancestor class ends in "renderer") is a best-effort heuristic, not a
 * stable contract with YouTube. If this stops finding cards, it needs to
 * be re-tuned against whatever markup YouTube is currently serving.
 */
(function () {
  var MARK = 'data-hftube-injected';

  function findCardRoot(anchor) {
    var el = anchor;
    for (var i = 0; i < 6 && el; i++) {
      if (el.className && typeof el.className === 'string' && /renderer/i.test(el.className)) {
        return el;
      }
      el = el.parentElement;
    }
    return anchor.parentElement || anchor;
  }

  function makeButton(url) {
    var btn = document.createElement('div');
    btn.textContent = '\u2913'; // simple down-arrow glyph as a placeholder icon
    btn.setAttribute('style',
      'display:inline-flex;align-items:center;justify-content:center;' +
      'width:22px;height:22px;margin-left:8px;border-radius:11px;' +
      'background:#2ED8A7;color:#121212;font-size:14px;font-weight:bold;' +
      'float:right;');
    btn.addEventListener('click', function (e) {
      e.preventDefault();
      e.stopPropagation();
      if (window.HFTube) {
        window.HFTube.onDownloadClicked(url);
      }
    });
    return btn;
  }

  function scan() {
    var anchors = document.querySelectorAll('a[href*="/watch?v="]');
    anchors.forEach(function (a) {
      var card = findCardRoot(a);
      if (card.getAttribute(MARK)) return;
      card.setAttribute(MARK, '1');
      var url = new URL(a.getAttribute('href'), location.href).href;
      card.appendChild(makeButton(url));
    });
  }

  scan();
  var observer = new MutationObserver(function () { scan(); });
  observer.observe(document.body, { childList: true, subtree: true });
})();
