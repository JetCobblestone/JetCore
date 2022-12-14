package me.jetcobblestone.util.tab;

import com.comphenix.protocol.wrappers.Pair;
import org.bukkit.ChatColor;

import java.util.HashMap;
import java.util.Map;

public class ColourMapper {
    private static final String[] colourNames = {"Red", "Orange", "Yellow", "Green", "Blue", "Pink"};
    private static final Map<String, ChatColor> colourMap = new HashMap<>();
    private static final Map<ChatColor, Pair<String, String>> colourSkinMap = new HashMap<>();

    static {
        colourMap.put("Red", ChatColor.RED);
        colourMap.put("Orange", ChatColor.GOLD);
        colourMap.put("Yellow", ChatColor.YELLOW);
        colourMap.put("Green", ChatColor.GREEN);
        colourMap.put("Blue", ChatColor.BLUE);
        colourMap.put("Pink", ChatColor.LIGHT_PURPLE);

        colourSkinMap.put(ChatColor.RED, new Pair<>("" +
                "ewogICJ0aW1lc3RhbXAiIDogMTYwMzQ1OTE4NTI2NiwKICAicHJvZmlsZUlkIiA6ICJjMWE3ZjlkZjgyYTU0NjZmOGQ2YjdkYTk3OTA4NGY0OSIsCiAgInByb2ZpbGVOYW1lIiA6ICJuZWFnZXIiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvM2M0ZDdhM2JjM2RlODMzZDMwMzJlODVhMGJmNmYyYmVmNzY4Nzg2MmIzYzZiYzQwY2U3MzEwNjRmNjE1ZGQ5ZCIKICAgIH0KICB9Cn0=",
                "VE/biydfEifVfXKerWfRaF17c9Pw/cSGhkw6WBWDcWiFq/825Z3L/q/Gi8xvalJVFjAdvPpKJIFRTDoDQoN/7Fikes7i4Z2DjwJDYOEaJpS3UcJeY+98N0Wu33Su/qlRL9xogotjkslMnvdMhnP/0yaJBZeG5UhzmqyRx9fqbAzauZTXrbIhl+nHB/OCV64y8iQ59TnmIWrFk3ByO9cOHla8cB1HW3a0f/ba5Vllj8hzRBTErFkeaixDJ1vo8dtADUfQaxoDyBSnsojMgplqXCWjfUwnq0d/VLKhg8zr8eSbmL4S/2uRrhKPVn0/VBhWQp+gB4sdgegt7/lr+ubHzrnDbcs5ho96yHjVh4F/igC2VNmsGjeRWOw9iE9nDeCOxnRAJjkFy0Ya0vgRflNb4mKoIirTxAEP1Q78Jyyjzb3UsDOnpTDQqIu9bUX9XCAKr5lfyyPqBpZabzyn3u1R7wnt1Hp2h1ZLoFShx+DHggFZYIzcoe+QJOTcqOr7w3QeHhjwUS4KLklBLVYL+0VxdNYg8zGre1NsOJkTu/TuVx2rmo7Avz59xc0gfoJFXWbzc00KYTxQUVr7CzhKq8UsiygmHfRizjjwljFkQINGHEPVtD8oNfdl1/FGleZbwjND5qT2dv3WyYDeywLGp2wkz1wBDw0LPBVKiv8mSFHGwhQ="
        ));
        colourSkinMap.put(ChatColor.GOLD, new Pair<>(
                "eyJ0aW1lc3RhbXAiOjE1Nzk1NTM0Njk0MzUsInByb2ZpbGVJZCI6IjkxZjA0ZmU5MGYzNjQzYjU4ZjIwZTMzNzVmODZkMzllIiwicHJvZmlsZU5hbWUiOiJTdG9ybVN0b3JteSIsInNpZ25hdHVyZVJlcXVpcmVkIjp0cnVlLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvM2NiZjJlZWI0ZDAyMjM2YjE1NTBhOGNmMTQ1Y2Q4NmZjNDkwMjYxNDI3MWUzZmMzNjA4ZTA3NTFmY2Y2YWU5MyJ9fX0="
                ,"vAm+/dbt26bvf8IBzIcyCEaHhHykLob2uYL338idMibIZOyhY09+OYOFVqv4A6VjcULcS4XkmMIGny6c4595nBVv6Czj0TN507ImyB2HtkCsR4WX2ut7hkwZGiDTVRODrgmh59O4bjJ2291e7v/MUI0v8dyuSCtjJi3oN2OL3B4NcxbYTJDTVDJU2YHMYm+soO8hkPIbC2A4XpON3xMFEFqTTi8win89uz9ZbeR/q4pI0JIAWqDFIl3022sXxboNXOK4NU8jeo7k4GoDXrSmiqXdfg4sJBV+WJFhcGmm0bTEYfbmDzqYhoIV0mtVRLyIPtTlW41uAsraZbcsYzFGbLXtRJO41nU8YRoKwtfhqkquKYtNTezghyM5WX7sKFIxD14l0hhhSAOuUvnJTs8iq/Z3RSws0WMk4xc+nnGJkJ1SuNIec6NPE6g3nY8gV6iuwkkAaZ0uOzTNula89RcpFiQoVg6kV95ZjtdKwgzJUx8fv36/eExSBiYAUS53PKeyTxeUO8EuuzAw0JNQ1mN+LJfk15TtIH0dC/oMDFM+6sfntx6DNb8ULFnOXuLPVvtVJKSgn/PRsIqtOTDokk1Uon5Hv51RWwX9HL3T6J7bG2Shh7j7jBSTXGBgC90nW9eM6VD6xa6kuzz6WA+66ftvBYtUjxVIy9Ba4OUc7zJnWoA="
        ));
        colourSkinMap.put(ChatColor.YELLOW, new Pair<>(
                "ewogICJ0aW1lc3RhbXAiIDogMTYwODY2NDkzNTE3NSwKICAicHJvZmlsZUlkIiA6ICI5ZDEzZjcyMTcxM2E0N2U0OTAwZTMyZGVkNjBjNDY3MyIsCiAgInByb2ZpbGVOYW1lIiA6ICJUYWxvZGFvIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2MyMjkxMzE0MjgzOTVlNTg0Mjg3YWM5MDIxZWI4YzNjYjIyYWU3MzI4NzFjMGUyMzAzNzU1MTM1NTE4NDQ4NzkiCiAgICB9CiAgfQp9",
                "e3sL5/mrL7qUj/jkkEBb1Khap2wUUcM/JGqEEZYBSE2FnwY7PI9acOnztk8MJMXvtmpPWKicRIAQmU/2ez062GTgEPEUcIjWB8R9pVo+U7756ZWcSYKsA368NJELdPy0txLli9keXsXQosQvnME/7VYhZxrYMxRlluErd2/YTtPnqu6+yyVfU+Q9Y8KfmsmWxD9U2GQjhSda/tYJSULWN4jLOse68TBlBFBa2CooaGKIxSRiEG8KfIvWlvbxGahAjWxMgmETq5Xm1d558WDyJvMJC56RQY5CqPgn0mz9yw8e6GjZQPyBeX2FbiYG/kcE6t9OiCC454fhxQZivXH2csK8pebROOX6+w7lT955XZLP9iKYINvNcfpqNJYGI1RCKOp4pPC3+dpW+ocfr5njm5iQG31TNU5z0JIq6sSZe8E9AyNTRNYNJ08q/ac8+nKFpVBDG0dkA1g0w/k/X8R+XI7J5nHtJYT5GUYCPzmRrakAXVQYtrnGWEhQ7SHp6woR9ZmKuscUfLQhIn05d6znHdkV0tYpTBg4DdoBnrmUmlwZ9BwHXTulntN3SAoMKdZ422TNiUqL63o8qRORhPm9aDtr1K083Gdhuj3hz+WdX9yKQSlT1XTKxQjivSQoWfP87yE1Zful2EDkEPpiGjcT580/7JeIWHXUo+JIcLMWqjs="
        ));
        colourSkinMap.put(ChatColor.GREEN, new Pair<>(
                "ewogICJ0aW1lc3RhbXAiIDogMTYxNTA4MjYyMTcxNiwKICAicHJvZmlsZUlkIiA6ICJmNWQwYjFhZTQxNmU0YTE5ODEyMTRmZGQzMWU3MzA1YiIsCiAgInByb2ZpbGVOYW1lIiA6ICJDYXRjaFRoZVdhdmUxMCIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS8yMDYyYjJkN2RhZGU1NzAwYmI4ZThkN2M0ZWNkMmZmNzMyZjliMzgwYWU1YzE2N2Q3Y2I0NWU1OWI3Y2EzZTVhIiwKICAgICAgIm1ldGFkYXRhIiA6IHsKICAgICAgICAibW9kZWwiIDogInNsaW0iCiAgICAgIH0KICAgIH0KICB9Cn0=",
                "bGkb+1Eo0jGokFCXOWX2V5eLsKDQRy2LgHklaVUIejG9GGlRe2XhgdA/fXTh1IkdL2Aq4A7BT9/B3g3ejjOuFIzTLpXc6Mj8lm8TLBXRu8rnHbSM0P4O2O5vAHAo84FVCdh0lNaBsaKR1FsWJL1fykbXjSjPHFQ89tcY+/6Vf5WuP/gvCpkay5mEoGy1krO5Khl4OS6Hx47kUloFr19BomBr4uK2EpAtIeoP9Xjcp4YI7h7+8JQx3Vm+wmUpnSFOvnilFdBZSD2QJuFG89FtrlpkrdC96luA/OcpVQ1aIHV7oaTkiGMEHmk+Un3RtY0cRONi3wtRF00G02a89BurzNAVJWC21t2YSnQVgXJnPEAJ7FqrQhk1Gpjm6uzXbPeUqDLBY33WKcWQUa3ztivVcx5RvY4rqNsyqky+ZQP7vHmLWeYrq3ld5MSgwY/XoCoZVSPphHyARukZQn9Y7zmFPQV0yAlSOZyyX6J7070G6covXyzD2YY3Tu+hPU6YKMTX78lRVQT3VfiWbEDqKGFVZ3zrfdTPuMvL4ysLS0ODDTnMu4TXUd7dPoFmiDHV4SUtFEK8v6Pwfat6SHgxt9lXGdyWQJNC5iAcn/HiMsz5tgRhloInEMPwPd7c6CROaRRuF91ICd/kmDzxxXfCPdJCYCIIGcdrTN9cUDTnXl7Pxqk="
        ));
        colourSkinMap.put(ChatColor.BLUE, new Pair<>(
                "ewogICJ0aW1lc3RhbXAiIDogMTYyNTY1MDE4MDE2NSwKICAicHJvZmlsZUlkIiA6ICI3MmNiMDYyMWU1MTA0MDdjOWRlMDA1OTRmNjAxNTIyZCIsCiAgInByb2ZpbGVOYW1lIiA6ICJNb3M5OTAiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmRhMDM2ZGM2YzZlZjI5ZjJmYTY3OTM0YjZkNTJjMzg3YmQxNjBlNDM4ZDFiMTE4Yzg0Y2FkNDRkZGZmNzgwYyIKICAgIH0KICB9Cn0=",
                "C6z8yV8tCMsE5PHm8XDegV7offeowVHOgsJ5Xsmlq7XQZaNihHucvaPtI/WxKdVY3slbmQwgXAlTQFo46hUwu62RxSFFK0G303hkKa3fJGUik0GiOL/STIzAFz41sXlZZQM03KNYTHL7CHwAmXNyCUFtCd1q4hCr5qowDxRc8tw/Zs2l02vTpy7kypuWQHK1au5jZuJ0AbNWWGiFB34GWOndkIK/MLfS3/CDHlS5P/AGHA3mbTqsa2/VhUI0HoR+RG8Vgtj78/XX3Jjpw1w3bQS2qtTsaUhK8+w2OrcfGwP+RA4dsFezoQwA1bgw6Q/29NrGfH3iXj4/x0YJ2xhsteoVIn/akR5c3yCFvRf4Cm2VsBEgBJB7OknlDY43wOIOfJ9QB7mefzChXYEVM5fdyJ0rQmICuMtEkjFu+b7k/uRmrYuTHqPGIu445gKOkCbYSrtmkV+mRO/YMZJl3ej1bAnlwzjMCdJeEV5ui8SNg+sZbdC8RMSEcmuX6U0ZKdyoLhe6+HyTNj80ubsIyyBS9VH6E/DAfPRehnjZ4NlQmBSyOu90DNjgOwqnrtpd9LuFVV3Z+kkQUiDljdqx1rg7LW+fdrl4wBLTEsABCpAYWrStmEXH57FqHDs4St7SEdzzgv1iisB1oHRMCYSDHD67myFk158Jcxmtu+QKcWCVwbI="
        ));
        colourSkinMap.put(ChatColor.LIGHT_PURPLE, new Pair<>(
                "eyJ0aW1lc3RhbXAiOjE1Nzk1NTE4MjIxMzcsInByb2ZpbGVJZCI6IjNmYzdmZGY5Mzk2MzRjNDE5MTE5OWJhM2Y3Y2MzZmVkIiwicHJvZmlsZU5hbWUiOiJZZWxlaGEiLCJzaWduYXR1cmVSZXF1aXJlZCI6dHJ1ZSwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzNjMjAwNjZjZmZiOTQ1MjE2OWU5NzBlMmQxZWM0NjM5ZmJkZTMzZmFiNDg3ODdkZThjMjllZjM4M2I0NTM5YWEifX19",
                "JlTjG4b+nIBNsCvRqG2Q/J3FTRxLE50zEX+QTonPMZjDcATvML3wZw9ldyf+Yfj4HaK96SQiVDpJqYIXqzZH9G2K1p0bltzAf8Z0c/oFlmmglXnMV5l1rR6IJpALDTW+Udw0XHbpJaFkVdwSGo36fTIdr6nnszCNbAKndAnLMsKeYL1SpvpjTvMCT2AIzVgpG+Wbrrj7luPJ0O7yfJ9w+p0rgc5bvA4B0xLaebPIKoPhKyzB7o4od5us70G+l0RaYdzJSISRCLQ9R2DvO3ExG0gR7elUR8DpCt3iB8rGHizBQMIg1UzJdXMNHmXo7YPN7OND8NDp5b7HAZNszVIS4RqOaT5KLXNacClXhRYMk36dgU9RUkuVpgzV4UOi2coHMWsniFgETn1D0FmpadMHw6f85GYFkUsASpHP+hUQlZGEpc2A10viid+9c3L0bzXg/dxdlk03etrFlA8WzOs6mAi5x4FGAHYCUHlgc80VrZdYy6nMF8TgFKsrWnJL46Tnki42jayNUy8OMEp15OMc4x8BuH6bkLI3qZdEl/AFKxAMWyHMuedtSHqtcAtjOgsZ34iQorEp93SjAfHZDiHe+VMdXMBCcG2zrDDaepMhwHoNTMw9v99z+UaT+wQG0X1m93l/jXel+pW7zXR1XntWbfuvVl0lNX+6T+vEden/bgA="
        ));
        colourSkinMap.put(ChatColor.GRAY, new Pair<>(
                "ewogICJ0aW1lc3RhbXAiIDogMTU5MDk0NjQ2NTU4MywKICAicHJvZmlsZUlkIiA6ICJiNzQ3OWJhZTI5YzQ0YjIzYmE1NjI4MzM3OGYwZTNjNiIsCiAgInByb2ZpbGVOYW1lIiA6ICJTeWxlZXgiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmUzMzE5NGJmMzNlOTYyZjM2YjVhYzEyZGU1NmY5N2JhZTU5MTAyZWE0NjRjNDU3MDliZTc3ZjcyOGQ3ZjBjZSIKICAgIH0KICB9Cn0=",
                "Ub7kyZWymsIa9cqZ2HHR2MNxBUcWcBl6ZfOQyvozUh3YXeyAgjxpM5lrSMALrAbawSLh2nVv9qoG/7/GhkymNx9/J+9jwTZo/r4COMF8nZeubXdIRc0KSxrErhqiMVb7Dg0PU4ULaZeeDcehDbhj6TiGrbRenlu/x8K9fNXXYebIGifAWNng7sIUpJfbbtXroNfhHtX/jSuBjGrz8/wCxDMZVnPGsd8YFRRKVM0qJT+DRQf0BBhcskJrblGOuOJFkKNcfXJUCTD118vpUGdHprD7cxtMPlh1WyWN/gi+eYzsE/EAjwe5NkB65xaEO3VTGD172+yfNU892hPlHtZ6QyhwIG7vh1iAY6dN21aA2i1U/mK9gVLVTYDjf7g6/V3HQ2Z372BUn7I4lCjNqnbkmPHoRYX1nwbNf3dY72y9lEN1NsnH83WKrvRQQhClM0pm3eQV3cWsJXUDNISgk3gNTDwRk9SxXtDc0L1YgbBF2uyhfxCooQgdkSFnqMOHm9lNNsCiZL2+isGEN8sEv0zTJ51txZeBUWyrw5iYQLPQMTYIL9/gXuzKhPEvKb9bRtcbvHVVZu60dpcpf5sqvoIFZ9+II1jNW7cG2x9gWd270ltkyhVdJh4/haiHykGh56CiEHnRb8w4ZuszY+/BB8Mp5brQKcpcDbiDJl+eCY/+JbM="
        ));
    }

    public static ChatColor colourFromString(String colourName) {
        return colourMap.get(colourName);
    }

    public static String[] getColourNames() {
        return colourNames;
    }

    public static Pair<String, String> getSkinfromColour(ChatColor colour) {
        return colourSkinMap.get(colour);
    }
}
