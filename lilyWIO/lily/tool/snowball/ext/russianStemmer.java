// This file was generated automatically by the Snowball to Java compiler

package lily.tool.snowball.ext;
import lily.tool.snowball.SnowballProgram;
import lily.tool.snowball.Among;

/**
 * Generated class implementing code defined by a snowball script.
 */
public class russianStemmer extends SnowballProgram {

        private Among a_0[] = {
            new Among ( "\u0432", -1, 1, "", this),
            new Among ( "\u0438\u0432", 0, 2, "", this),
            new Among ( "\u044B\u0432", 0, 2, "", this),
            new Among ( "\u0432\u0448\u0438", -1, 1, "", this),
            new Among ( "\u0438\u0432\u0448\u0438", 3, 2, "", this),
            new Among ( "\u044B\u0432\u0448\u0438", 3, 2, "", this),
            new Among ( "\u0432\u0448\u0438\u0441\u044C", -1, 1, "", this),
            new Among ( "\u0438\u0432\u0448\u0438\u0441\u044C", 6, 2, "", this),
            new Among ( "\u044B\u0432\u0448\u0438\u0441\u044C", 6, 2, "", this)
        };

        private Among a_1[] = {
            new Among ( "\u0435\u0435", -1, 1, "", this),
            new Among ( "\u0438\u0435", -1, 1, "", this),
            new Among ( "\u043E\u0435", -1, 1, "", this),
            new Among ( "\u044B\u0435", -1, 1, "", this),
            new Among ( "\u0438\u043C\u0438", -1, 1, "", this),
            new Among ( "\u044B\u043C\u0438", -1, 1, "", this),
            new Among ( "\u0435\u0439", -1, 1, "", this),
            new Among ( "\u0438\u0439", -1, 1, "", this),
            new Among ( "\u043E\u0439", -1, 1, "", this),
            new Among ( "\u044B\u0439", -1, 1, "", this),
            new Among ( "\u0435\u043C", -1, 1, "", this),
            new Among ( "\u0438\u043C", -1, 1, "", this),
            new Among ( "\u043E\u043C", -1, 1, "", this),
            new Among ( "\u044B\u043C", -1, 1, "", this),
            new Among ( "\u0435\u0433\u043E", -1, 1, "", this),
            new Among ( "\u043E\u0433\u043E", -1, 1, "", this),
            new Among ( "\u0435\u043C\u0443", -1, 1, "", this),
            new Among ( "\u043E\u043C\u0443", -1, 1, "", this),
            new Among ( "\u0438\u0445", -1, 1, "", this),
            new Among ( "\u044B\u0445", -1, 1, "", this),
            new Among ( "\u0435\u044E", -1, 1, "", this),
            new Among ( "\u043E\u044E", -1, 1, "", this),
            new Among ( "\u0443\u044E", -1, 1, "", this),
            new Among ( "\u044E\u044E", -1, 1, "", this),
            new Among ( "\u0430\u044F", -1, 1, "", this),
            new Among ( "\u044F\u044F", -1, 1, "", this)
        };

        private Among a_2[] = {
            new Among ( "\u0435\u043C", -1, 1, "", this),
            new Among ( "\u043D\u043D", -1, 1, "", this),
            new Among ( "\u0432\u0448", -1, 1, "", this),
            new Among ( "\u0438\u0432\u0448", 2, 2, "", this),
            new Among ( "\u044B\u0432\u0448", 2, 2, "", this),
            new Among ( "\u0449", -1, 1, "", this),
            new Among ( "\u044E\u0449", 5, 1, "", this),
            new Among ( "\u0443\u044E\u0449", 6, 2, "", this)
        };

        private Among a_3[] = {
            new Among ( "\u0441\u044C", -1, 1, "", this),
            new Among ( "\u0441\u044F", -1, 1, "", this)
        };

        private Among a_4[] = {
            new Among ( "\u043B\u0430", -1, 1, "", this),
            new Among ( "\u0438\u043B\u0430", 0, 2, "", this),
            new Among ( "\u044B\u043B\u0430", 0, 2, "", this),
            new Among ( "\u043D\u0430", -1, 1, "", this),
            new Among ( "\u0435\u043D\u0430", 3, 2, "", this),
            new Among ( "\u0435\u0442\u0435", -1, 1, "", this),
            new Among ( "\u0438\u0442\u0435", -1, 2, "", this),
            new Among ( "\u0439\u0442\u0435", -1, 1, "", this),
            new Among ( "\u0435\u0439\u0442\u0435", 7, 2, "", this),
            new Among ( "\u0443\u0439\u0442\u0435", 7, 2, "", this),
            new Among ( "\u043B\u0438", -1, 1, "", this),
            new Among ( "\u0438\u043B\u0438", 10, 2, "", this),
            new Among ( "\u044B\u043B\u0438", 10, 2, "", this),
            new Among ( "\u0439", -1, 1, "", this),
            new Among ( "\u0435\u0439", 13, 2, "", this),
            new Among ( "\u0443\u0439", 13, 2, "", this),
            new Among ( "\u043B", -1, 1, "", this),
            new Among ( "\u0438\u043B", 16, 2, "", this),
            new Among ( "\u044B\u043B", 16, 2, "", this),
            new Among ( "\u0435\u043C", -1, 1, "", this),
            new Among ( "\u0438\u043C", -1, 2, "", this),
            new Among ( "\u044B\u043C", -1, 2, "", this),
            new Among ( "\u043D", -1, 1, "", this),
            new Among ( "\u0435\u043D", 22, 2, "", this),
            new Among ( "\u043B\u043E", -1, 1, "", this),
            new Among ( "\u0438\u043B\u043E", 24, 2, "", this),
            new Among ( "\u044B\u043B\u043E", 24, 2, "", this),
            new Among ( "\u043D\u043E", -1, 1, "", this),
            new Among ( "\u0435\u043D\u043E", 27, 2, "", this),
            new Among ( "\u043D\u043D\u043E", 27, 1, "", this),
            new Among ( "\u0435\u0442", -1, 1, "", this),
            new Among ( "\u0443\u0435\u0442", 30, 2, "", this),
            new Among ( "\u0438\u0442", -1, 2, "", this),
            new Among ( "\u044B\u0442", -1, 2, "", this),
            new Among ( "\u044E\u0442", -1, 1, "", this),
            new Among ( "\u0443\u044E\u0442", 34, 2, "", this),
            new Among ( "\u044F\u0442", -1, 2, "", this),
            new Among ( "\u043D\u044B", -1, 1, "", this),
            new Among ( "\u0435\u043D\u044B", 37, 2, "", this),
            new Among ( "\u0442\u044C", -1, 1, "", this),
            new Among ( "\u0438\u0442\u044C", 39, 2, "", this),
            new Among ( "\u044B\u0442\u044C", 39, 2, "", this),
            new Among ( "\u0435\u0448\u044C", -1, 1, "", this),
            new Among ( "\u0438\u0448\u044C", -1, 2, "", this),
            new Among ( "\u044E", -1, 2, "", this),
            new Among ( "\u0443\u044E", 44, 2, "", this)
        };

        private Among a_5[] = {
            new Among ( "\u0430", -1, 1, "", this),
            new Among ( "\u0435\u0432", -1, 1, "", this),
            new Among ( "\u043E\u0432", -1, 1, "", this),
            new Among ( "\u0435", -1, 1, "", this),
            new Among ( "\u0438\u0435", 3, 1, "", this),
            new Among ( "\u044C\u0435", 3, 1, "", this),
            new Among ( "\u0438", -1, 1, "", this),
            new Among ( "\u0435\u0438", 6, 1, "", this),
            new Among ( "\u0438\u0438", 6, 1, "", this),
            new Among ( "\u0430\u043C\u0438", 6, 1, "", this),
            new Among ( "\u044F\u043C\u0438", 6, 1, "", this),
            new Among ( "\u0438\u044F\u043C\u0438", 10, 1, "", this),
            new Among ( "\u0439", -1, 1, "", this),
            new Among ( "\u0435\u0439", 12, 1, "", this),
            new Among ( "\u0438\u0435\u0439", 13, 1, "", this),
            new Among ( "\u0438\u0439", 12, 1, "", this),
            new Among ( "\u043E\u0439", 12, 1, "", this),
            new Among ( "\u0430\u043C", -1, 1, "", this),
            new Among ( "\u0435\u043C", -1, 1, "", this),
            new Among ( "\u0438\u0435\u043C", 18, 1, "", this),
            new Among ( "\u043E\u043C", -1, 1, "", this),
            new Among ( "\u044F\u043C", -1, 1, "", this),
            new Among ( "\u0438\u044F\u043C", 21, 1, "", this),
            new Among ( "\u043E", -1, 1, "", this),
            new Among ( "\u0443", -1, 1, "", this),
            new Among ( "\u0430\u0445", -1, 1, "", this),
            new Among ( "\u044F\u0445", -1, 1, "", this),
            new Among ( "\u0438\u044F\u0445", 26, 1, "", this),
            new Among ( "\u044B", -1, 1, "", this),
            new Among ( "\u044C", -1, 1, "", this),
            new Among ( "\u044E", -1, 1, "", this),
            new Among ( "\u0438\u044E", 30, 1, "", this),
            new Among ( "\u044C\u044E", 30, 1, "", this),
            new Among ( "\u044F", -1, 1, "", this),
            new Among ( "\u0438\u044F", 33, 1, "", this),
            new Among ( "\u044C\u044F", 33, 1, "", this)
        };

        private Among a_6[] = {
            new Among ( "\u043E\u0441\u0442", -1, 1, "", this),
            new Among ( "\u043E\u0441\u0442\u044C", -1, 1, "", this)
        };

        private Among a_7[] = {
            new Among ( "\u0435\u0439\u0448\u0435", -1, 1, "", this),
            new Among ( "\u043D", -1, 2, "", this),
            new Among ( "\u0435\u0439\u0448", -1, 1, "", this),
            new Among ( "\u044C", -1, 3, "", this)
        };

        private static final char g_v[] = {33, 65, 8, 232 };

        private int I_p2;
        private int I_pV;

        private void copy_from(russianStemmer other) {
            I_p2 = other.I_p2;
            I_pV = other.I_pV;
            super.copy_from(other);
        }

        private boolean r_mark_regions() {
            int v_1;
            // (, line 57
            I_pV = limit;
            I_p2 = limit;
            // do, line 61
            v_1 = cursor;
            lab0: do {
                // (, line 61
                // gopast, line 62
                golab1: while(true)
                {
                    lab2: do {
                        if (!(in_grouping(g_v, 1072, 1103)))
                        {
                            break lab2;
                        }
                        break golab1;
                    } while (false);
                    if (cursor >= limit)
                    {
                        break lab0;
                    }
                    cursor++;
                }
                // setmark pV, line 62
                I_pV = cursor;
                // gopast, line 62
                golab3: while(true)
                {
                    lab4: do {
                        if (!(out_grouping(g_v, 1072, 1103)))
                        {
                            break lab4;
                        }
                        break golab3;
                    } while (false);
                    if (cursor >= limit)
                    {
                        break lab0;
                    }
                    cursor++;
                }
                // gopast, line 63
                golab5: while(true)
                {
                    lab6: do {
                        if (!(in_grouping(g_v, 1072, 1103)))
                        {
                            break lab6;
                        }
                        break golab5;
                    } while (false);
                    if (cursor >= limit)
                    {
                        break lab0;
                    }
                    cursor++;
                }
                // gopast, line 63
                golab7: while(true)
                {
                    lab8: do {
                        if (!(out_grouping(g_v, 1072, 1103)))
                        {
                            break lab8;
                        }
                        break golab7;
                    } while (false);
                    if (cursor >= limit)
                    {
                        break lab0;
                    }
                    cursor++;
                }
                // setmark p2, line 63
                I_p2 = cursor;
            } while (false);
            cursor = v_1;
            return true;
        }

        private boolean r_R2() {
            if (!(I_p2 <= cursor))
            {
                return false;
            }
            return true;
        }

        private boolean r_perfective_gerund() {
            int among_var;
            int v_1;
            // (, line 71
            // [, line 72
            ket = cursor;
            // substring, line 72
            among_var = find_among_b(a_0, 9);
            if (among_var == 0)
            {
                return false;
            }
            // ], line 72
            bra = cursor;
            switch(among_var) {
                case 0:
                    return false;
                case 1:
                    // (, line 76
                    // or, line 76
                    lab0: do {
                        v_1 = limit - cursor;
                        lab1: do {
                            // literal, line 76
                            if (!(eq_s_b(1, "\u0430")))
                            {
                                break lab1;
                            }
                            break lab0;
                        } while (false);
                        cursor = limit - v_1;
                        // literal, line 76
                        if (!(eq_s_b(1, "\u044F")))
                        {
                            return false;
                        }
                    } while (false);
                    // delete, line 76
                    slice_del();
                    break;
                case 2:
                    // (, line 83
                    // delete, line 83
                    slice_del();
                    break;
            }
            return true;
        }

        private boolean r_adjective() {
            int among_var;
            // (, line 87
            // [, line 88
            ket = cursor;
            // substring, line 88
            among_var = find_among_b(a_1, 26);
            if (among_var == 0)
            {
                return false;
            }
            // ], line 88
            bra = cursor;
            switch(among_var) {
                case 0:
                    return false;
                case 1:
                    // (, line 97
                    // delete, line 97
                    slice_del();
                    break;
            }
            return true;
        }

        private boolean r_adjectival() {
            int among_var;
            int v_1;
            int v_2;
            // (, line 101
            // call adjective, line 102
            if (!r_adjective())
            {
                return false;
            }
            // try, line 109
            v_1 = limit - cursor;
            lab0: do {
                // (, line 109
                // [, line 110
                ket = cursor;
                // substring, line 110
                among_var = find_among_b(a_2, 8);
                if (among_var == 0)
                {
                    cursor = limit - v_1;
                    break lab0;
                }
                // ], line 110
                bra = cursor;
                switch(among_var) {
                    case 0:
                        cursor = limit - v_1;
                        break lab0;
                    case 1:
                        // (, line 115
                        // or, line 115
                        lab1: do {
                            v_2 = limit - cursor;
                            lab2: do {
                                // literal, line 115
                                if (!(eq_s_b(1, "\u0430")))
                                {
                                    break lab2;
                                }
                                break lab1;
                            } while (false);
                            cursor = limit - v_2;
                            // literal, line 115
                            if (!(eq_s_b(1, "\u044F")))
                            {
                                cursor = limit - v_1;
                                break lab0;
                            }
                        } while (false);
                        // delete, line 115
                        slice_del();
                        break;
                    case 2:
                        // (, line 122
                        // delete, line 122
                        slice_del();
                        break;
                }
            } while (false);
            return true;
        }

        private boolean r_reflexive() {
            int among_var;
            // (, line 128
            // [, line 129
            ket = cursor;
            // substring, line 129
            among_var = find_among_b(a_3, 2);
            if (among_var == 0)
            {
                return false;
            }
            // ], line 129
            bra = cursor;
            switch(among_var) {
                case 0:
                    return false;
                case 1:
                    // (, line 132
                    // delete, line 132
                    slice_del();
                    break;
            }
            return true;
        }

        private boolean r_verb() {
            int among_var;
            int v_1;
            // (, line 136
            // [, line 137
            ket = cursor;
            // substring, line 137
            among_var = find_among_b(a_4, 46);
            if (among_var == 0)
            {
                return false;
            }
            // ], line 137
            bra = cursor;
            switch(among_var) {
                case 0:
                    return false;
                case 1:
                    // (, line 143
                    // or, line 143
                    lab0: do {
                        v_1 = limit - cursor;
                        lab1: do {
                            // literal, line 143
                            if (!(eq_s_b(1, "\u0430")))
                            {
                                break lab1;
                            }
                            break lab0;
                        } while (false);
                        cursor = limit - v_1;
                        // literal, line 143
                        if (!(eq_s_b(1, "\u044F")))
                        {
                            return false;
                        }
                    } while (false);
                    // delete, line 143
                    slice_del();
                    break;
                case 2:
                    // (, line 151
                    // delete, line 151
                    slice_del();
                    break;
            }
            return true;
        }

        private boolean r_noun() {
            int among_var;
            // (, line 159
            // [, line 160
            ket = cursor;
            // substring, line 160
            among_var = find_among_b(a_5, 36);
            if (among_var == 0)
            {
                return false;
            }
            // ], line 160
            bra = cursor;
            switch(among_var) {
                case 0:
                    return false;
                case 1:
                    // (, line 167
                    // delete, line 167
                    slice_del();
                    break;
            }
            return true;
        }

        private boolean r_derivational() {
            int among_var;
            // (, line 175
            // [, line 176
            ket = cursor;
            // substring, line 176
            among_var = find_among_b(a_6, 2);
            if (among_var == 0)
            {
                return false;
            }
            // ], line 176
            bra = cursor;
            // call R2, line 176
            if (!r_R2())
            {
                return false;
            }
            switch(among_var) {
                case 0:
                    return false;
                case 1:
                    // (, line 179
                    // delete, line 179
                    slice_del();
                    break;
            }
            return true;
        }

        private boolean r_tidy_up() {
            int among_var;
            // (, line 183
            // [, line 184
            ket = cursor;
            // substring, line 184
            among_var = find_among_b(a_7, 4);
            if (among_var == 0)
            {
                return false;
            }
            // ], line 184
            bra = cursor;
            switch(among_var) {
                case 0:
                    return false;
                case 1:
                    // (, line 188
                    // delete, line 188
                    slice_del();
                    // [, line 189
                    ket = cursor;
                    // literal, line 189
                    if (!(eq_s_b(1, "\u043D")))
                    {
                        return false;
                    }
                    // ], line 189
                    bra = cursor;
                    // literal, line 189
                    if (!(eq_s_b(1, "\u043D")))
                    {
                        return false;
                    }
                    // delete, line 189
                    slice_del();
                    break;
                case 2:
                    // (, line 192
                    // literal, line 192
                    if (!(eq_s_b(1, "\u043D")))
                    {
                        return false;
                    }
                    // delete, line 192
                    slice_del();
                    break;
                case 3:
                    // (, line 194
                    // delete, line 194
                    slice_del();
                    break;
            }
            return true;
        }

        public boolean stem() {
            int v_1;
            int v_2;
            int v_3;
            int v_4;
            int v_5;
            int v_6;
            int v_7;
            int v_8;
            int v_9;
            int v_10;
            // (, line 199
            // do, line 201
            v_1 = cursor;
            lab0: do {
                // call mark_regions, line 201
                if (!r_mark_regions())
                {
                    break lab0;
                }
            } while (false);
            cursor = v_1;
            // backwards, line 202
            limit_backward = cursor; cursor = limit;
            // setlimit, line 202
            v_2 = limit - cursor;
            // tomark, line 202
            if (cursor < I_pV)
            {
                return false;
            }
            cursor = I_pV;
            v_3 = limit_backward;
            limit_backward = cursor;
            cursor = limit - v_2;
            // (, line 202
            // do, line 203
            v_4 = limit - cursor;
            lab1: do {
                // (, line 203
                // or, line 204
                lab2: do {
                    v_5 = limit - cursor;
                    lab3: do {
                        // call perfective_gerund, line 204
                        if (!r_perfective_gerund())
                        {
                            break lab3;
                        }
                        break lab2;
                    } while (false);
                    cursor = limit - v_5;
                    // (, line 205
                    // try, line 205
                    v_6 = limit - cursor;
                    lab4: do {
                        // call reflexive, line 205
                        if (!r_reflexive())
                        {
                            cursor = limit - v_6;
                            break lab4;
                        }
                    } while (false);
                    // or, line 206
                    lab5: do {
                        v_7 = limit - cursor;
                        lab6: do {
                            // call adjectival, line 206
                            if (!r_adjectival())
                            {
                                break lab6;
                            }
                            break lab5;
                        } while (false);
                        cursor = limit - v_7;
                        lab7: do {
                            // call verb, line 206
                            if (!r_verb())
                            {
                                break lab7;
                            }
                            break lab5;
                        } while (false);
                        cursor = limit - v_7;
                        // call noun, line 206
                        if (!r_noun())
                        {
                            break lab1;
                        }
                    } while (false);
                } while (false);
            } while (false);
            cursor = limit - v_4;
            // try, line 209
            v_8 = limit - cursor;
            lab8: do {
                // (, line 209
                // [, line 209
                ket = cursor;
                // literal, line 209
                if (!(eq_s_b(1, "\u0438")))
                {
                    cursor = limit - v_8;
                    break lab8;
                }
                // ], line 209
                bra = cursor;
                // delete, line 209
                slice_del();
            } while (false);
            // do, line 212
            v_9 = limit - cursor;
            lab9: do {
                // call derivational, line 212
                if (!r_derivational())
                {
                    break lab9;
                }
            } while (false);
            cursor = limit - v_9;
            // do, line 213
            v_10 = limit - cursor;
            lab10: do {
                // call tidy_up, line 213
                if (!r_tidy_up())
                {
                    break lab10;
                }
            } while (false);
            cursor = limit - v_10;
            limit_backward = v_3;
            cursor = limit_backward;            return true;
        }

}

