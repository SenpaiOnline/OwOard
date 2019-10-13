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
    QUOTATION_MARK("\u0022"),
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
    HYPHEN_MINUS("\u002d")
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
