package org.privacyguides.verifiedapps.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp

const val APACHE2LICENSE =
        "\n" +
        "Apache License\n" +
        "Version 2.0, January 2004\n" +
        "http://www.apache.org/licenses/\n" +
        "\n" +
        "TERMS AND CONDITIONS FOR USE, REPRODUCTION, AND DISTRIBUTION\n" +
        "\n" +
        "1. Definitions.\n" +
        "\n" +
        "\"License\" shall mean the terms and conditions for use, reproduction, and distribution as " +
        "defined by Sections 1 through 9 of this document.\n" +
        "\n" +
        "\"Licensor\" shall mean the copyright owner or entity authorized by the copyright owner that " +
        "is granting the License.\n" +
        "\n" +
        "\"Legal Entity\" shall mean the union of the acting entity and all other entities that " +
        "control, are controlled by, or are under common control with that entity. For the purposes " +
        "of this definition, \"control\" means (i) the power, direct or indirect, to cause the " +
        "direction or management of such entity, whether by contract or otherwise, or (ii) ownership " +
        "of fifty percent (50%) or more of the outstanding shares, or (iii) beneficial ownership of " +
        "such entity.\n" +
        "\n" +
        "\"You\" (or \"Your\") shall mean an individual or Legal Entity exercising permissions granted by " +
        "this License.\n" +
        "\n" +
        "\"Source\" form shall mean the preferred form for making modifications, including but not " +
        "limited to software source code, documentation source, and configuration files.\n" +
        "\n" +
        "\"Object\" form shall mean any form resulting from mechanical transformation or translation of " +
        "a Source form, including but not limited to compiled object code, generated documentation, " +
        "and conversions to other media types.\n" +
        "\n" +
        "\"Work\" shall mean the work of authorship, whether in Source or Object form, made available " +
        "under the License, as indicated by a copyright notice that is included in or attached to the " +
        "work (an example is provided in the Appendix below).\n" +
        "\n" +
        "\"Derivative Works\" shall mean any work, whether in Source or Object form, that is based on " +
        "(or derived from) the Work and for which the editorial revisions, annotations, elaborations, " +
        "or other modifications represent, as a whole, an original work of authorship. For the " +
        "purposes of this License, Derivative Works shall not include works that remain separable " +
        "from, or merely link (or bind by name) to the interfaces of, the Work and Derivative Works " +
        "thereof.\n" +
        "\n" +
        "\"Contribution\" shall mean any work of authorship, including the original version of the Work " +
        "and any modifications or additions to that Work or Derivative Works thereof, that is " +
        "intentionally submitted to Licensor for inclusion in the Work by the copyright owner or by " +
        "an individual or Legal Entity authorized to submit on behalf of the copyright owner. For the " +
        "purposes of this definition, \"submitted\" means any form of electronic, verbal, or written " +
        "communication sent to the Licensor or its representatives, including but not limited to " +
        "communication on electronic mailing lists, source code control systems, and issue tracking " +
        "systems that are managed by, or on behalf of, the Licensor for the purpose of discussing and " +
        "improving the Work, but excluding communication that is conspicuously marked or otherwise " +
        "designated in writing by the copyright owner as \"Not a Contribution.\"\n" +
        "\n" +
        "\"Contributor\" shall mean Licensor and any individual or Legal Entity on behalf of whom a " +
        "Contribution has been received by Licensor and subsequently incorporated within the Work.\n" +
        "\n" +
        "2. Grant of Copyright License. Subject to the terms and conditions of this License, each " +
        "Contributor hereby grants to You a perpetual, worldwide, non-exclusive, no-charge, " +
        "royalty-free, irrevocable copyright license to reproduce, prepare Derivative Works of, " +
        "publicly display, publicly perform, sublicense, and distribute the Work and such Derivative " +
        "Works in Source or Object form.\n" +
        "\n" +
        "3. Grant of Patent License. Subject to the terms and conditions of this License, each " +
        "Contributor hereby grants to You a perpetual, worldwide, non-exclusive, no-charge, " +
        "royalty-free, irrevocable (except as stated in this section) patent license to make, have " +
        "made, use, offer to sell, sell, import, and otherwise transfer the Work, where such license " +
        "applies only to those patent claims licensable by such Contributor that are necessarily " +
        "infringed by their Contribution(s) alone or by combination of their Contribution(s) with the " +
        "Work to which such Contribution(s) was submitted. If You institute patent litigation against " +
        "any entity (including a cross-claim or counterclaim in a lawsuit) alleging that the Work or " +
        "a Contribution incorporated within the Work constitutes direct or contributory patent " +
        "infringement, then any patent licenses granted to You under this License for that Work shall " +
        "terminate as of the date such litigation is filed.\n" +
        "\n" +
        "4. Redistribution. You may reproduce and distribute copies of the Work or Derivative Works " +
        "thereof in any medium, with or without modifications, and in Source or Object form, provided " +
        "that You meet the following conditions:\n" +
        "\n" +
        "(a) You must give any other recipients of the Work or Derivative Works a copy of this " +
        "License; and\n" +
        "\n" +
        "(b) You must cause any modified files to carry prominent notices stating that You changed " +
        "the files; and\n" +
        "\n" +
        "(c) You must retain, in the Source form of any Derivative Works that You distribute, all " +
        "copyright, patent, trademark, and attribution notices from the Source form of the Work, " +
        "excluding those notices that do not pertain to any part of the Derivative Works; and\n" +
        "\n" +
        "(d) If the Work includes a \"NOTICE\" text file as part of its distribution, then any " +
        "Derivative Works that You distribute must include a readable copy of the attribution notices " +
        "contained within such NOTICE file, excluding those notices that do not pertain to any part " +
        "of the Derivative Works, in at least one of the following places: within a NOTICE text file " +
        "distributed as part of the Derivative Works; within the Source form or documentation, if " +
        "provided along with the Derivative Works; or, within a display generated by the Derivative " +
        "Works, if and wherever such third-party notices normally appear. The contents of the NOTICE " +
        "file are for informational purposes only and do not modify the License. You may add Your own " +
        "attribution notices within Derivative Works that You distribute, alongside or as an addendum " +
        "to the NOTICE text from the Work, provided that such additional attribution notices cannot " +
        "be construed as modifying the License.\n" +
        "\n" +
        "You may add Your own copyright statement to Your modifications and may provide additional or " +
        "different license terms and conditions for use, reproduction, or distribution of Your " +
        "modifications, or for any such Derivative Works as a whole, provided Your use, reproduction, " +
        "and distribution of the Work otherwise complies with the conditions stated in this License.\n" +
        "\n" +
        "5. Submission of Contributions. Unless You explicitly state otherwise, any Contribution " +
        "intentionally submitted for inclusion in the Work by You to the Licensor shall be under the " +
        "terms and conditions of this License, without any additional terms or conditions. " +
        "Notwithstanding the above, nothing herein shall supersede or modify the terms of any " +
        "separate license agreement you may have executed with Licensor regarding such Contributions.\n" +
        "\n" +
        "6. Trademarks. This License does not grant permission to use the trade names, trademarks, " +
        "service marks, or product names of the Licensor, except as required for reasonable and " +
        "customary use in describing the origin of the Work and reproducing the content of the NOTICE " +
        "file.\n" +
        "\n" +
        "7. Disclaimer of Warranty. Unless required by applicable law or agreed to in writing, " +
        "Licensor provides the Work (and each Contributor provides its Contributions) on an \"AS IS\" " +
        "BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied, including, " +
        "without limitation, any warranties or conditions of TITLE, NON-INFRINGEMENT, " +
        "MERCHANTABILITY, or FITNESS FOR A PARTICULAR PURPOSE. You are solely responsible for " +
        "determining the appropriateness of using or redistributing the Work and assume any risks " +
        "associated with Your exercise of permissions under this License.\n" +
        "\n" +
        "8. Limitation of Liability. In no event and under no legal theory, whether in tort " +
        "(including negligence), contract, or otherwise, unless required by applicable law (such as " +
        "deliberate and grossly negligent acts) or agreed to in writing, shall any Contributor be " +
        "liable to You for damages, including any direct, indirect, special, incidental, or " +
        "consequential damages of any character arising as a result of this License or out of the use " +
        "or inability to use the Work (including but not limited to damages for loss of goodwill, " +
        "work stoppage, computer failure or malfunction, or any and all other commercial damages or " +
        "losses), even if such Contributor has been advised of the possibility of such damages.\n" +
        "\n" +
        "9. Accepting Warranty or Additional Liability. While redistributing the Work or Derivative " +
        "Works thereof, You may choose to offer, and charge a fee for, acceptance of support, " +
        "warranty, indemnity, or other liability obligations and/or rights consistent with this " +
        "License. However, in accepting such obligations, You may act only on Your own behalf and on " +
        "Your sole responsibility, not on behalf of any other Contributor, and only if You agree to " +
        "indemnify, defend, and hold each Contributor harmless for any liability incurred by, or " +
        "claims asserted against, such Contributor by reason of your accepting any such warranty or " +
        "additional liability.\n" +
        "\n" +
        "END OF TERMS AND CONDITIONS\n" +
        "\n" +
        "APPENDIX: How to apply the Apache License to your work.\n" +
        "\n" +
        "To apply the Apache License to your work, attach the following boilerplate notice, with the " +
        "fields enclosed by brackets \"[]\" replaced with your own identifying information. (Don't " +
        "include the brackets!)  The text should be enclosed in the appropriate comment syntax for " +
        "the file format. We also recommend that a file or class name and description of purpose be " +
        "included on the same \"printed page\" as the copyright notice for easier identification within " +
        "third-party archives.\n" +
        "\n" +
        "Copyright [yyyy] [name of copyright owner]\n" +
        "\n" +
        "Licensed under the Apache License, Version 2.0 (the \"License\"); you may not use this file " +
        "except in compliance with the License. You may obtain a copy of the License at\n" +
        "\n" +
        "http://www.apache.org/licenses/LICENSE-2.0\n" +
        "\n" +
        "Unless required by applicable law or agreed to in writing, software distributed under the " +
        "License is distributed on an \"AS IS\" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, " +
        "either express or implied. See the License for the specific language governing permissions " +
        "and limitations under the License.\n"

private const val ISC_LICENSE_TERMS =
        "Permission to use, copy, modify, and/or distribute this software for any purpose with or " +
        "without fee is hereby granted, provided that the above copyright notice and this permission " +
        "notice appear in all copies.\n" +
        "\n" +
        "THE SOFTWARE IS PROVIDED \"AS IS\" AND THE AUTHOR DISCLAIMS ALL WARRANTIES WITH REGARD TO THIS " +
        "SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL " +
        "THE AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY " +
        "DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN ACTION OF " +
        "CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE " +
        "OR PERFORMANCE OF THIS SOFTWARE.\n"

private fun iscLicense(copyrightNotice: String): String =
    "\nISC License\n\n$copyrightNotice\n\n$ISC_LICENSE_TERMS"

private val ISC_LICENSE_SOUPSURPR =
    iscLicense("Copyright (c) 2024-2025 soupslurpr")

private val ISC_LICENSE_SOUPSURPR_ROUNDSALMON =
    iscLicense(
        "Copyright (c) 2024-2025 soupslurpr\nCopyright (c) 2026 RoundSalmon4",
    )

private const val MIT_LICENSE =
    "\nMIT License\n" +
        "\n" +
        "Copyright (c) 2026 Jonah Aragon\n" +
        "Copyright (c) 2026 Privacy Guides & contributors\n" +
        "\n" +
        "Permission is hereby granted, free of charge, to any person obtaining a copy " +
        "of this software and associated documentation files (the \"Software\"), to deal " +
        "in the Software without restriction, including without limitation the rights " +
        "to use, copy, modify, merge, publish, distribute, sublicense, and/or sell " +
        "copies of the Software, and to permit persons to whom the Software is " +
        "furnished to do so, subject to the following conditions:\n" +
        "\n" +
        "The above copyright notice and this permission notice shall be included in all " +
        "copies or substantial portions of the Software.\n" +
        "\n" +
        "THE SOFTWARE IS PROVIDED \"AS IS\", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR " +
        "IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, " +
        "FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE " +
        "AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER " +
        "LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, " +
        "OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE " +
        "SOFTWARE."

@Composable
fun CreditsScreen() {
    LazyColumn(
        modifier = Modifier
    ) {
        item {
            CreditsItem(
                dependencyName = "Verified Apps Database",
                dependencyPackageName = "Crowdsourced Android app signature data",
                dependencyLicense = MIT_LICENSE,
            )
        }
        item {
            CreditsItem(
                dependencyName = "AppVerifier",
                dependencyPackageName = "dev.soupslurpr.appverifier",
                dependencyLicense = ISC_LICENSE_SOUPSURPR,
            )
        }
        item {
            CreditsItem(
                dependencyName = "AppVerifierBG",
                dependencyPackageName = "com.roundsalmon4.appverifier",
                dependencyLicense = ISC_LICENSE_SOUPSURPR_ROUNDSALMON,
            )
        }
        item {
            CreditsItem(
                dependencyName = "Core Ktx",
                dependencyPackageName = "androidx.core:core-ktx",
                dependencyLicense = APACHE2LICENSE,
            )
        }
        item {
            CreditsItem(
                dependencyName = "Lifecycle ViewModel Ktx",
                dependencyPackageName = "androidx.lifecycle:lifecycle-viewmodel-ktx",
                dependencyLicense = APACHE2LICENSE,
            )
        }
        item {
            CreditsItem(
                dependencyName = "Kotlin Coroutines Android",
                dependencyPackageName = "org.jetbrains.kotlinx:kotlinx-coroutines-android",
                dependencyLicense = APACHE2LICENSE,
            )
        }
        item {
            CreditsItem(
                dependencyName = "Activity Compose",
                dependencyPackageName = "androidx.activity:activity-compose",
                dependencyLicense = APACHE2LICENSE,
            )
        }
        item {
            CreditsItem(
                dependencyName = "Compose Navigation",
                dependencyPackageName = "androidx.navigation:navigation-compose",
                dependencyLicense = APACHE2LICENSE,
            )
        }
        item {
            CreditsItem(
                dependencyName = "Android Preferences DataStore",
                dependencyPackageName = "androidx.datastore:datastore-preferences",
                dependencyLicense = APACHE2LICENSE,
            )
        }
        item {
            CreditsItem(
                dependencyName = "Accompanist DrawablePainter",
                dependencyPackageName = "com.google.accompanist:accompanist-drawablepainter",
                dependencyLicense = APACHE2LICENSE,
            )
        }
        item {
            CreditsItem(
                dependencyName = "Jetpack Compose BOM",
                dependencyPackageName = "androidx.compose:compose-bom",
                dependencyLicense = APACHE2LICENSE,
            )
        }
        item {
            CreditsItem(
                dependencyName = "Compose Material3 Components",
                dependencyPackageName = "androidx.compose.material3:material3",
                dependencyLicense = APACHE2LICENSE,
            )
        }
        item {
            CreditsItem(
                dependencyName = "Material Symbols",
                dependencyPackageName = "androidx.compose.material:material-icons-extended",
                dependencyLicense = APACHE2LICENSE,
            )
        }
        item {
            CreditsItem(
                dependencyName = "Compose UI Preview Tooling",
                dependencyPackageName = "androidx.compose.ui:ui-tooling-preview",
                dependencyLicense = APACHE2LICENSE,
            )
        }


        // This MUST stay at the bottom or else when fully scrolled some credits will be blocked.
        item {
            Spacer(Modifier.padding(WindowInsets.navigationBars.asPaddingValues()))
        }
    }
}

@Composable
fun CreditsItem(
    dependencyName: String,
    dependencyPackageName: String,
    dependencyLicense: String,
) {
    var dropped by rememberSaveable { mutableStateOf(false) }

    ListItem(
        modifier = Modifier.clickable(
            onClickLabel = "View $dependencyName's license",
            role = Role.DropdownList,
            onClick = { dropped = !dropped },
        ),
        headlineContent = { Text(text = dependencyName) },
        supportingContent = { Text(text = dependencyPackageName) },
        trailingContent = {
            Icon(imageVector = Icons.Filled.Info, contentDescription = null)
        }
    )
    if (dropped) {
        Text(
            modifier = Modifier.padding(horizontal = 16.dp),
            style = typography.bodySmall,
            text = dependencyLicense,
        )
    }
}