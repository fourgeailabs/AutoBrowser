package com.example.data.model

data class AutomationPreset(
  val id: String,
  val title: String,
  val description: String,
  val iconName: String,
  val script: String
)

object DefaultAutomations {
  val list = listOf(
    AutomationPreset(
      id = "dark_mode",
      title = "Force High-Contrast Dark",
      description = "Applies dark theme styles to reduce glare on light pages",
      iconName = "dark_mode",
      script = """
        (function() {
          const styleId = 'autobrowser-dark-theme';
          const existing = document.getElementById(styleId);
          if (existing) {
            existing.remove();
            return 'Dark mode removed';
          }
          const css = `
            html { filter: invert(90%) hue-rotate(180deg) !important; background: #121212 !important; }
            img, video, iframe, canvas, svg { filter: invert(100%) hue-rotate(180deg) !important; }
          `;
          const style = document.createElement('style');
          style.id = styleId;
          style.appendChild(document.createTextNode(css));
          document.head.appendChild(style);
          return 'Dark mode applied';
        })();
      """.trimIndent()
    ),
    AutomationPreset(
      id = "clean_overlays",
      title = "Dismiss Sticky Overlays",
      description = "Removes floating banners, cookie consents, and sticky headers",
      iconName = "cleaning_services",
      script = """
        (function() {
          let count = 0;
          const elements = document.querySelectorAll('div, section, aside, header');
          elements.forEach(el => {
            const style = window.getComputedStyle(el);
            if (style.position === 'fixed' || style.position === 'sticky') {
              const rect = el.getBoundingClientRect();
              if (rect.height < 250 || rect.width > window.innerWidth * 0.8) {
                el.style.display = 'none';
                count++;
              }
            }
          });
          document.body.style.overflow = 'auto';
          return 'Dismissed ' + count + ' sticky overlay elements';
        })();
      """.trimIndent()
    ),
    AutomationPreset(
      id = "reader_mode",
      title = "Reader Focus Clean-up",
      description = "Hides ads, navbars, sidebars to focus purely on text reading",
      iconName = "menu_book",
      script = """
        (function() {
          const selectors = ['header', 'footer', 'nav', 'aside', '.ad', '.ads', '.sidebar', '.social-share'];
          selectors.forEach(sel => {
            document.querySelectorAll(sel).forEach(e => e.style.display = 'none');
          });
          document.body.style.maxWidth = '800px';
          document.body.style.margin = '0 auto';
          document.body.style.padding = '16px';
          document.body.style.lineHeight = '1.7';
          document.body.style.fontSize = '18px';
          return 'Reader mode activated';
        })();
      """.trimIndent()
    ),
    AutomationPreset(
      id = "extract_links",
      title = "Extract Page Hyperlinks",
      description = "Collects all distinct external hyperlinks on current page",
      iconName = "link",
      script = """
        (function() {
          const links = Array.from(document.querySelectorAll('a[href]'))
            .map(a => a.href)
            .filter(h => h.startsWith('http'))
            .slice(0, 15);
          return 'Found ' + links.length + ' links:\n' + links.join('\n');
        })();
      """.trimIndent()
    ),
    AutomationPreset(
      id = "scroll_to_top",
      title = "Smooth Jump to Top",
      description = "Instantly scrolls the viewport smoothly back to the top",
      iconName = "vertical_align_top",
      script = """
        (function() {
          window.scrollTo({ top: 0, behavior: 'smooth' });
          return 'Scrolled to top';
        })();
      """.trimIndent()
    ),
    AutomationPreset(
      id = "scroll_to_bottom",
      title = "Smooth Jump to Bottom",
      description = "Instantly scrolls the viewport to page footer",
      iconName = "vertical_align_bottom",
      script = """
        (function() {
          window.scrollTo({ top: document.body.scrollHeight, behavior: 'smooth' });
          return 'Scrolled to bottom';
        })();
      """.trimIndent()
    )
  )
}
