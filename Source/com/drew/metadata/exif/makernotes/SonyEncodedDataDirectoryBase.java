package com.drew.metadata.exif.makernotes;

import com.drew.metadata.Directory;

public abstract class SonyEncodedDataDirectoryBase extends Directory
{
    private static final byte[] _substitution = new byte[]
        {
            0, 1, 50, (byte)177, 10, 14, (byte)135, 40, 2, (byte)204, (byte)202, (byte)173, 27, (byte)220, 8, (byte)237, 100,
            (byte)134, (byte)240, 79, (byte)140, 108, (byte)184, (byte)203, 105, (byte)196, 44, 3, (byte)151, (byte)182,
            (byte)147, 124, 20, (byte)243, (byte)226, 62, 48, (byte)142, (byte)215, 96, 28, (byte)161, (byte)171, 55,
            (byte)236, 117, (byte)190, 35, 21, 106, 89, 63, (byte)208, (byte)185, (byte)150, (byte)181, 80, 39, (byte)136,
            (byte)227, (byte)129, (byte)148, (byte)224, (byte)192, 4, 92, (byte)198, (byte)232, 95, 75, 112, 56, (byte)159,
            (byte)130, (byte)128, 81, 43, (byte)197, 69, 73, (byte)155, 33, 82, 83, 84, (byte)133, 11, 93, 97, (byte)218,
            123, 85, 38, 36, 7, 110, 54, 91, 71, (byte)183, (byte)217, 74, (byte)162, (byte)223, (byte)191, 18, 37,
            (byte)188, 30, 127, 86, (byte)234, 16, (byte)230, (byte)207, 103, 77, 60, (byte)145, (byte)131, (byte)225, 49,
            (byte)179, 111, (byte)244, 5, (byte)138, 70, (byte)200, 24, 118, 104, (byte)189, (byte)172, (byte)146, 42, 19,
            (byte)233, 15, (byte)163, 122, (byte)219, 61, (byte)212, (byte)231, 58, 26, 87, (byte)175, 32, 66, (byte)178,
            (byte)158, (byte)195, (byte)139, (byte)242, (byte)213, (byte)211, (byte)164, 126, 31, (byte)152, (byte)156,
            (byte)238, 116, (byte)165, (byte)166, (byte)167, (byte)216, 94, (byte)176, (byte)180, 52, (byte)206, (byte)168,
            121, 119, 90, (byte)193, (byte)137, (byte)174, (byte)154, 17, 51, (byte)157, (byte)245, 57, 25, 101, 120, 22,
            113, (byte)210, (byte)169, 68, 99, 64, 41, (byte)186, (byte)160, (byte)143, (byte)228, (byte)214, 59, (byte)132,
            13, (byte)194, 78, 88, (byte)221, (byte)153, 34, 107, (byte)201, (byte)187, 23, 6, (byte)229, 125, 102, 67, 98,
            (byte)246, (byte)205, 53, (byte)144, 46, 65, (byte)141, 109, (byte)170, 9, 115, (byte)149, 12, (byte)241, 29,
            (byte)222, 76, 47, 45, (byte)247, (byte)209, 114, (byte)235, (byte)239, 72, (byte)199, (byte)248, (byte)249,
            (byte)250, (byte)251, (byte)252, (byte)253, (byte)254, (byte)255
        };

    protected static void decipherInPlace(byte[] bytes)
    {
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = _substitution[bytes[i] & 0xFF];
        }
    }
}
