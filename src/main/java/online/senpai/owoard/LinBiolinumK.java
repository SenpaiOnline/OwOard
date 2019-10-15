/*
 * This file is part of the OwOard distribution (https://github.com/aiscy/OwOard).
 * Copyright (c) 2019 Maxim Valeryevich Pavlov.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package online.senpai.owoard;

import de.jensd.fx.glyphs.GlyphIcons;

public enum LinBiolinumK implements GlyphIcons { // Currently kotlin has problem with interfaces contains function called "name" :/

    EXCLAMATION_MARK("\u0021"),
    QUOTATION_MARK("\u005c\u0022"),
    NUMBER_SIGN("\u0023"),
    DOLLAR_SIGN("\u0024"),
    PERCENT_SIGN("\u0025"),
    AMPERSAND("\u0026"),
    APOSTROPHE("\u0027"),
    LEFT_PARENTHESIS("\u0028"),
    RIGHT_PARENTHESIS("\u0029"),
    ASTERISK("\u002a"),
    PLUS_SIGN("\u002b"),
    COMMA("\u002c"),
    HYPHEN_MINUS("\u002d"),
    PERIOD("\u002e"),
    SLASH("\u002f"),
    ZERO("\u0030"),
    ONE("\u0031"),
    TWO("\u0032"),
    THREE("\u0033"),
    FOUR("\u0034"),
    FIVE("\u0035"),
    SIX("\u0036"),
    SEVEN("\u0037"),
    EIGHT("\u0038"),
    NINE("\u0039"),
    COLON("\u003a"),
    SEMICOLON("\u003b"),
    LESS("\u003c"),
    EQUAL("\u003d"),
    GREATER("\u003e"),
    QUESTION_MARK("\u003f"),
    AT("\u0040"),
    A("\u0041"),
    B("\u0042"),
    C("\u0043"),
    D("\u0044"),
    E("\u0045"),
    F("\u0046"),
    G("\u0047"),
    H("\u0048"),
    I("\u0049"),
    J("\u004a"),
    K("\u004b"),
    L("\u004c"),
    M("\u004d"),
    N("\u004e"),
    O("\u004f"),
    P("\u0050"),
    Q("\u0051"),
    R("\u0052"),
    S("\u0053"),
    T("\u0054"),
    U("\u0055"),
    V("\u0056"),
    W("\u0057"),
    X("\u0058"),
    Y("\u0059"),
    Z("\u005a"),
    BRACKET_LEFT("\u005b"),
    BACKSLASH("\u005c\u005c"),
    BRACKET_RIGHT("\u005d"),
    ASCIICIRCUM("\u005e"),
    UNDERSCORE("\u005f"),
    GRAVE("\u0060"),
    ARROW_LEFT("\u2190"),
    ARROW_UP("\u2191"),
    ARROW_RIGHT("\u2192"),
    ARROW_DOWN("\u2193"),
    MAC("\u2318"),
    TUX("\ue000"),
    WIN("\ue168"),
    BACKSPACE("\ue16e"),
    ALT("\ue171"),
    CTRL("\ue173"),
    SHIFT("\ue174"),
    TAB("\ue175"),
    ENTER("\ue176"),
    CAPSLOCK("\ue177"),
    F1("\ue178"),
    F2("\ue179"),
    F3("\ue17a"),
    F4("\ue17b"),
    F5("\ue17c"),
    F6("\ue17d"),
    F7("\ue17e"),
    F8("\ue17f"),
    F9("\ue180"),
    F10("\ue181"),
    F11("\ue182"),
    F12("\ue183"),
    F13("\ue184"),
    F14("\ue185"),
    F15("\ue186"),
    F16("\ue187"),
    FN("\ue188"),
    HOME("\ue189"),
    DEL("\ue18a"),
    INS("\ue18b"),
    SPACE("\ue18c"),
    END("\ue18e"),
    ESC("\ue195"),
    PAGE_UP("\ue19a"),
    PAGE_DOWN("\ue19b"),
    PAD0("\ue1a0"),
    PAD1("\ue1a1"),
    PAD2("\ue1a2"),
    PAD3("\ue1a3"),
    PAD4("\ue1a4"),
    PAD5("\ue1a5"),
    PAD6("\ue1a6"),
    PAD7("\ue1a7"),
    PAD8("\ue1a8"),
    PAD9("\ue1a9"),
    PAD_DIVIDE("\ue1aa"),
    PAD_PLUS("\ue1ab"),
    PAD_SUBTRACT("\ue1ac"),
    PAD_MULTIPLY("\ue1ad"),
    PAD_ENTER("\ue1ae"),
    ;

    private final String unicode;

    private LinBiolinumK(String unicode) {
        this.unicode = unicode;
    }

    @Override
    public String unicode() {
        return unicode;
    }

    @Override
    public String fontFamily() {
        return "\'Linux Biolinum Keyboard\'";
    }
}
