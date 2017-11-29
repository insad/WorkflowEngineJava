/*
Company: OptimaJet
Project: WF.Sample WorkflowEngine.NET
File: FillData.sql
*/

BEGIN TRANSACTION

INSERT dbo.Roles(Id, Name) VALUES ('8d378ebe-0666-46b3-b7ab-1a52480fd12a', N'Big Boss')
INSERT dbo.Roles(Id, Name) VALUES ('412174c2-0490-4101-a7b3-830de90bcaa0', N'Accountant')
INSERT dbo.Roles(Id, Name) VALUES ('71fffb5b-b707-4b3c-951c-c37fdfcc8dfb', N'User')

INSERT dbo.StructDivision(Id, Name, ParentId) VALUES ('f6e34bdf-b769-42dd-a2be-fee67faf9045', N'Head Group', NULL)
INSERT dbo.StructDivision(Id, Name, ParentId) VALUES ('b14f5d81-5b0d-4acc-92b8-27cbbe39086b', N'Group 1', 'f6e34bdf-b769-42dd-a2be-fee67faf9045')
INSERT dbo.StructDivision(Id, Name, ParentId) VALUES ('7e9fd972-c775-4c6b-9d91-47e9397bd2e6', N'Group 1.1', 'b14f5d81-5b0d-4acc-92b8-27cbbe39086b')
INSERT dbo.StructDivision(Id, Name, ParentId) VALUES ('dc195a4f-46f9-41b2-80d2-77ff9c6269b7', N'Group 1.2', 'b14f5d81-5b0d-4acc-92b8-27cbbe39086b')
INSERT dbo.StructDivision(Id, Name, ParentId) VALUES ('72d461b2-234b-40d6-b410-b261964ba291', N'Group 2', 'f6e34bdf-b769-42dd-a2be-fee67faf9045')
INSERT dbo.StructDivision(Id, Name, ParentId) VALUES ('c5dcc148-9c0c-45c4-8a68-901d99a26184', N'Group 2.2', '72d461b2-234b-40d6-b410-b261964ba291')
INSERT dbo.StructDivision(Id, Name, ParentId) VALUES ('bc21a482-28e7-4951-8177-e57813a70fc5', N'Group 2.1', '72d461b2-234b-40d6-b410-b261964ba291')


INSERT dbo.Employee(Id, Name, StructDivisionId, IsHead) VALUES ('81537e21-91c5-4811-a546-2dddff6bf409', N'Silviya', 'f6e34bdf-b769-42dd-a2be-fee67faf9045', 1)
INSERT dbo.Employee(Id, Name, StructDivisionId, IsHead) VALUES ('b0e6fd4c-2db9-4bb6-a62e-68b6b8999905', N'Margo', 'dc195a4f-46f9-41b2-80d2-77ff9c6269b7', 0)
INSERT dbo.Employee(Id, Name, StructDivisionId, IsHead) VALUES ('deb579f9-991c-4db9-a17d-bb1eccf2842c', N'Max', 'b14f5d81-5b0d-4acc-92b8-27cbbe39086b', 1)
INSERT dbo.Employee(Id, Name, StructDivisionId, IsHead) VALUES ('91f2b471-4a96-4ab7-a41a-ea4293703d16', N'John', '7e9fd972-c775-4c6b-9d91-47e9397bd2e6', 1)
INSERT dbo.Employee(Id, Name, StructDivisionId, IsHead) VALUES ('e41b48e3-c03d-484f-8764-1711248c4f8a', N'Maria', 'c5dcc148-9c0c-45c4-8a68-901d99a26184', 0)
INSERT dbo.Employee(Id, Name, StructDivisionId, IsHead) VALUES ('bbe686f8-8736-48a7-a886-2da25567f978', N'Mark', '7e9fd972-c775-4c6b-9d91-47e9397bd2e6', 0)

INSERT dbo.EmployeeRole(EmloyeeId, RoleId) VALUES ('e41b48e3-c03d-484f-8764-1711248c4f8a', '412174c2-0490-4101-a7b3-830de90bcaa0')
INSERT dbo.EmployeeRole(EmloyeeId, RoleId) VALUES ('e41b48e3-c03d-484f-8764-1711248c4f8a', '71fffb5b-b707-4b3c-951c-c37fdfcc8dfb')
INSERT dbo.EmployeeRole(EmloyeeId, RoleId) VALUES ('bbe686f8-8736-48a7-a886-2da25567f978', '71fffb5b-b707-4b3c-951c-c37fdfcc8dfb')
INSERT dbo.EmployeeRole(EmloyeeId, RoleId) VALUES ('81537e21-91c5-4811-a546-2dddff6bf409', '8d378ebe-0666-46b3-b7ab-1a52480fd12a')
INSERT dbo.EmployeeRole(EmloyeeId, RoleId) VALUES ('81537e21-91c5-4811-a546-2dddff6bf409', '71fffb5b-b707-4b3c-951c-c37fdfcc8dfb')
INSERT dbo.EmployeeRole(EmloyeeId, RoleId) VALUES ('b0e6fd4c-2db9-4bb6-a62e-68b6b8999905', '71fffb5b-b707-4b3c-951c-c37fdfcc8dfb')
INSERT dbo.EmployeeRole(EmloyeeId, RoleId) VALUES ('deb579f9-991c-4db9-a17d-bb1eccf2842c', '71fffb5b-b707-4b3c-951c-c37fdfcc8dfb')
INSERT dbo.EmployeeRole(EmloyeeId, RoleId) VALUES ('91f2b471-4a96-4ab7-a41a-ea4293703d16', '71fffb5b-b707-4b3c-951c-c37fdfcc8dfb')



EXEC(N'INSERT dbo.WorkflowScheme(Code, Scheme) VALUES (N''SimpleWF'', N''
<Process Name="SimpleWF">
  <Designer Scale="" X="-110" Y="-60"/>
  <Actors>
    <Actor Name="Author" Rule="IsDocumentAuthor" Value=""/>
    <Actor Name="AuthorsBoss" Rule="IsAuthorsBoss" Value=""/>
    <Actor Name="Controller" Rule="IsDocumentController" Value=""/>
    <Actor Name="BigBoss" Rule="CheckRole" Value="Big Boss"/>
    <Actor Name="Accountant" Rule="CheckRole" Value="Accountant"/>
  </Actors>
  <Parameters>
    <Parameter Name="Comment" Purpose="Temporary" Type="java.lang.String"/>
  </Parameters>
  <Commands>
    <Command Name="StartProcessing">
      <InputParameters>
        <ParameterRef DefaultValue="" IsRequired="False" Name="Comment" NameRef="Comment"/>
      </InputParameters>
    </Command>
    <Command Name="Sighting">
      <InputParameters>
        <ParameterRef DefaultValue="" IsRequired="False" Name="Comment" NameRef="Comment"/>
      </InputParameters>
    </Command>
    <Command Name="Denial">
      <InputParameters>
        <ParameterRef DefaultValue="" IsRequired="False" Name="Comment" NameRef="Comment"/>
      </InputParameters>
    </Command>
    <Command Name="Paid">
      <InputParameters>
        <ParameterRef DefaultValue="" IsRequired="False" Name="Comment" NameRef="Comment"/>
      </InputParameters>
    </Command>
  </Commands>
  <Timers>
    <Timer Name="ControllerTimer" NotOverrideIfExists="False" Type="Interval" Value="120000"/>
  </Timers>
  <Activities>
    <Activity IsAutoSchemeUpdate="False" IsFinal="False" IsForSetState="False" IsInitial="True" Name="DraftInitial" State="Draft">
      <PreExecutionImplementation>
        <ActionRef NameRef="WriteTransitionHistory" Order="1"/>
      </PreExecutionImplementation>
      <Designer X="50" Y="90"/>
    </Activity>
    <Activity IsAutoSchemeUpdate="True" IsFinal="False" IsForSetState="True" IsInitial="False" Name="Draft" State="Draft">
      <Implementation>
        <ActionRef NameRef="UpdateTransitionHistory" Order="1"/>
      </Implementation>
      <PreExecutionImplementation>
        <ActionRef NameRef="WriteTransitionHistory" Order="1"/>
      </PreExecutionImplementation>
      <Designer X="50" Y="350"/>
    </Activity>
    <Activity IsAutoSchemeUpdate="False" IsFinal="False" IsForSetState="False" IsInitial="False" Name="DraftStartProcessingExecute">
      <Designer X="340" Y="90"/>
    </Activity>
    <Activity IsAutoSchemeUpdate="True" IsFinal="False" IsForSetState="True" IsInitial="False" Name="ControllerSighting" State="ControllerSighting">
      <Implementation>
        <ActionRef NameRef="UpdateTransitionHistory" Order="1"/>
      </Implementation>
      <PreExecutionImplementation>
        <ActionRef NameRef="WriteTransitionHistory" Order="1"/>
      </PreExecutionImplementation>
      <Designer X="360" Y="240"/>
    </Activity>
    <Activity IsAutoSchemeUpdate="False" IsFinal="False" IsForSetState="False" IsInitial="False" Name="ControllerSightingExecute">
      <Designer X="670" Y="90"/>
    </Activity>
    <Activity IsAutoSchemeUpdate="True" IsFinal="False" IsForSetState="True" IsInitial="False" Name="AuthorBossSighting" State="AuthorBossSighting">
      <Implementation>
        <ActionRef NameRef="UpdateTransitionHistory" Order="1"/>
      </Implementation>
      <PreExecutionImplementation>
        <ActionRef NameRef="WriteTransitionHistory" Order="1"/>
      </PreExecutionImplementation>
      <Designer X="590" Y="240"/>
    </Activity>
    <Activity IsAutoSchemeUpdate="True" IsFinal="False" IsForSetState="True" IsInitial="False" Name="AuthorConfirmation" State="AuthorConfirmation">
      <Implementation>
        <ActionRef NameRef="UpdateTransitionHistory" Order="1"/>
      </Implementation>
      <PreExecutionImplementation>
        <ActionRef NameRef="WriteTransitionHistory" Order="1"/>
      </PreExecutionImplementation>
      <Designer X="860" Y="240"/>
    </Activity>
    <Activity IsAutoSchemeUpdate="True" IsFinal="False" IsForSetState="True" IsInitial="False" Name="BigBossSighting" State="BigBossSighting">
      <Implementation>
        <ActionRef NameRef="UpdateTransitionHistory" Order="1"/>
      </Implementation>
      <PreExecutionImplementation>
        <ActionRef NameRef="WriteTransitionHistory" Order="1"/>
      </PreExecutionImplementation>
      <Designer X="850" Y="360"/>
    </Activity>
    <Activity IsAutoSchemeUpdate="True" IsFinal="False" IsForSetState="True" IsInitial="False" Name="AccountantProcessing" State="AccountantProcessing">
      <Implementation>
        <ActionRef NameRef="UpdateTransitionHistory" Order="1"/>
      </Implementation>
      <PreExecutionImplementation>
        <ActionRef NameRef="WriteTransitionHistory" Order="1"/>
      </PreExecutionImplementation>
      <Designer X="1120" Y="240"/>
    </Activity>
    <Activity IsAutoSchemeUpdate="True" IsFinal="True" IsForSetState="True" IsInitial="False" Name="Paid" State="Paid">
      <Implementation>
        <ActionRef NameRef="UpdateTransitionHistory" Order="1"/>
      </Implementation>
      <PreExecutionImplementation>
        <ActionRef NameRef="WriteTransitionHistory" Order="1"/>
      </PreExecutionImplementation>
      <Designer X="1110" Y="90"/>
    </Activity>
  </Activities>
  <Transitions>
    <Transition AllowConcatenationType="And" Classifier="Direct" ConditionsConcatenationType="And" DisableParentStateControl="False" From="DraftInitial" IsFork="False" MergeViaSetState="False" Name="DraftInitial" RestrictConcatenationType="And" To="DraftStartProcessingExecute">
      <Restrictions>
        <Restriction NameRef="Author" Type="Allow"/>
      </Restrictions>
      <Triggers>
        <Trigger NameRef="StartProcessing" Type="Command"/>
      </Triggers>
      <Conditions>
        <Condition Type="Always"/>
      </Conditions>
      <Designer Bending="" X="293.75000000000006" Y="120.00000000000006"/>
    </Transition>
    <Transition AllowConcatenationType="And" Classifier="Direct" ConditionsConcatenationType="And" DisableParentStateControl="False" From="Draft" IsFork="False" MergeViaSetState="False" Name="Draft" RestrictConcatenationType="And" To="ControllerSighting">
      <Restrictions>
        <Restriction NameRef="Author" Type="Allow"/>
      </Restrictions>
      <Triggers>
        <Trigger NameRef="StartProcessing" Type="Command"/>
      </Triggers>
      <Conditions>
        <Condition Type="Always"/>
      </Conditions>
      <Designer Bending="" X="203" Y="267"/>
    </Transition>
    <Transition AllowConcatenationType="And" Classifier="Direct" ConditionsConcatenationType="And" DisableParentStateControl="False" From="DraftStartProcessingExecute" IsFork="False" MergeViaSetState="False" Name="DraftStartProcessingExecute_1" RestrictConcatenationType="And" To="ControllerSighting">
      <Triggers>
        <Trigger Type="Auto"/>
      </Triggers>
      <Conditions>
        <Condition ConditionInversion="False" NameRef="CheckDocumentHasController" Type="Action"/>
      </Conditions>
      <Designer Bending="" X="447.5" Y="190.0000000000001"/>
    </Transition>
    <Transition AllowConcatenationType="And" Classifier="Direct" ConditionsConcatenationType="And" DisableParentStateControl="False" From="DraftStartProcessingExecute" IsFork="False" MergeViaSetState="False" Name="DraftStartProcessingExecute_2" RestrictConcatenationType="And" To="ControllerSightingExecute">
      <Triggers>
        <Trigger Type="Auto"/>
      </Triggers>
      <Conditions>
        <Condition Type="Otherwise"/>
      </Conditions>
      <Designer Bending="" X="595" Y="120"/>
    </Transition>
    <Transition AllowConcatenationType="And" Classifier="Direct" ConditionsConcatenationType="And" DisableParentStateControl="False" From="ControllerSighting" IsFork="False" MergeViaSetState="False" Name="ControllerSighting" RestrictConcatenationType="And" To="ControllerSightingExecute">
      <Restrictions>
        <Restriction NameRef="Controller" Type="Allow"/>
      </Restrictions>
      <Triggers>
        <Trigger NameRef="Sighting" Type="Command"/>
      </Triggers>
      <Conditions>
        <Condition Type="Always"/>
      </Conditions>
      <Designer Bending="" X="579" Y="190"/>
    </Transition>
    <Transition AllowConcatenationType="And" Classifier="Reverse" ConditionsConcatenationType="And" DisableParentStateControl="False" From="ControllerSighting" IsFork="False" MergeViaSetState="False" Name="ControllerSighting_R" RestrictConcatenationType="And" To="Draft">
      <Restrictions>
        <Restriction NameRef="Controller" Type="Allow"/>
      </Restrictions>
      <Triggers>
        <Trigger NameRef="Denial" Type="Command"/>
      </Triggers>
      <Conditions>
        <Condition Type="Always"/>
      </Conditions>
      <Designer Bending="" X="456" Y="339"/>
    </Transition>
    <Transition AllowConcatenationType="And" Classifier="Direct" ConditionsConcatenationType="And" DisableParentStateControl="False" From="ControllerSightingExecute" IsFork="False" MergeViaSetState="False" Name="ControllerSightingExecute_1" RestrictConcatenationType="And" To="AuthorConfirmation">
      <Triggers>
        <Trigger Type="Auto"/>
      </Triggers>
      <Conditions>
        <Condition ConditionInversion="False" NameRef="CheckDocumentsAuthorIsBoss" Type="Action"/>
      </Conditions>
      <Designer Bending="" X="955" Y="116"/>
    </Transition>
    <Transition AllowConcatenationType="And" Classifier="Direct" ConditionsConcatenationType="And" DisableParentStateControl="False" From="ControllerSightingExecute" IsFork="False" MergeViaSetState="False" Name="ControllerSightingExecute_2" RestrictConcatenationType="And" To="AuthorBossSighting">
      <Triggers>
        <Trigger Type="Auto"/>
      </Triggers>
      <Conditions>
        <Condition Type="Otherwise"/>
      </Conditions>
      <Designer Bending="" X="695" Y="193"/>
    </Transition>
    <Transition AllowConcatenationType="And" Classifier="Direct" ConditionsConcatenationType="And" DisableParentStateControl="False" From="AuthorBossSighting" IsFork="False" MergeViaSetState="False" Name="AuthorBossSighting" RestrictConcatenationType="And" To="AuthorConfirmation">
      <Restrictions>
        <Restriction NameRef="AuthorsBoss" Type="Allow"/>
      </Restrictions>
      <Triggers>
        <Trigger NameRef="Sighting" Type="Command"/>
      </Triggers>
      <Conditions>
        <Condition Type="Always"/>
      </Conditions>
      <Designer Bending="" X="813" Y="195"/>
    </Transition>
    <Transition AllowConcatenationType="And" Classifier="Reverse" ConditionsConcatenationType="And" DisableParentStateControl="False" From="AuthorBossSighting" IsFork="False" MergeViaSetState="False" Name="AuthorBossSighting_R" RestrictConcatenationType="And" To="Draft">
      <Restrictions>
        <Restriction NameRef="AuthorsBoss" Type="Allow"/>
      </Restrictions>
      <Triggers>
        <Trigger NameRef="Denial" Type="Command"/>
      </Triggers>
      <Conditions>
        <Condition Type="Always"/>
      </Conditions>
      <Designer Bending="" X="677" Y="377"/>
    </Transition>
    <Transition AllowConcatenationType="And" Classifier="Direct" ConditionsConcatenationType="And" DisableParentStateControl="False" From="AuthorConfirmation" IsFork="False" MergeViaSetState="False" Name="AuthorConfirmation_1" RestrictConcatenationType="And" To="BigBossSighting">
      <Restrictions>
        <Restriction NameRef="Author" Type="Allow"/>
      </Restrictions>
      <Triggers>
        <Trigger NameRef="Sighting" Type="Command"/>
      </Triggers>
      <Conditions>
        <Condition ConditionInversion="False" NameRef="CheckBigBossMustSight" Type="Action"/>
      </Conditions>
      <Designer Bending="" X="912" Y="326"/>
    </Transition>
    <Transition AllowConcatenationType="And" Classifier="Direct" ConditionsConcatenationType="And" DisableParentStateControl="False" From="AuthorConfirmation" IsFork="False" MergeViaSetState="False" Name="AuthorConfirmation_2" RestrictConcatenationType="And" To="AccountantProcessing">
      <Restrictions>
        <Restriction NameRef="Author" Type="Allow"/>
      </Restrictions>
      <Triggers>
        <Trigger NameRef="Sighting" Type="Command"/>
      </Triggers>
      <Conditions>
        <Condition Type="Otherwise"/>
      </Conditions>
      <Designer Bending="" X="1080" Y="193"/>
    </Transition>
    <Transition AllowConcatenationType="And" Classifier="Reverse" ConditionsConcatenationType="And" DisableParentStateControl="False" From="AuthorConfirmation" IsFork="False" MergeViaSetState="False" Name="AuthorConfirmation_R" RestrictConcatenationType="And" To="Draft">
      <Restrictions>
        <Restriction NameRef="Author" Type="Allow"/>
      </Restrictions>
      <Triggers>
        <Trigger NameRef="Denial" Type="Command"/>
      </Triggers>
      <Conditions>
        <Condition Type="Always"/>
      </Conditions>
      <Designer Bending="" X="818" Y="397"/>
    </Transition>
    <Transition AllowConcatenationType="And" Classifier="Direct" ConditionsConcatenationType="And" DisableParentStateControl="False" From="BigBossSighting" IsFork="False" MergeViaSetState="False" Name="BigBossSighting" RestrictConcatenationType="And" To="AccountantProcessing">
      <Restrictions>
        <Restriction NameRef="BigBoss" Type="Allow"/>
      </Restrictions>
      <Triggers>
        <Trigger NameRef="Sighting" Type="Command"/>
      </Triggers>
      <Conditions>
        <Condition Type="Always"/>
      </Conditions>
      <Designer Bending="" X="1215" Y="346"/>
    </Transition>
    <Transition AllowConcatenationType="And" Classifier="Reverse" ConditionsConcatenationType="And" DisableParentStateControl="False" From="BigBossSighting" IsFork="False" MergeViaSetState="False" Name="BigBossSighting_R" RestrictConcatenationType="And" To="Draft">
      <Restrictions>
        <Restriction NameRef="BigBoss" Type="Allow"/>
      </Restrictions>
      <Triggers>
        <Trigger NameRef="Denial" Type="Command"/>
      </Triggers>
      <Conditions>
        <Condition Type="Always"/>
      </Conditions>
      <Designer Bending="" X="538" Y="484"/>
    </Transition>
    <Transition AllowConcatenationType="And" Classifier="Direct" ConditionsConcatenationType="And" DisableParentStateControl="False" From="AccountantProcessing" IsFork="False" MergeViaSetState="False" Name="AccountantProcessing" RestrictConcatenationType="And" To="Paid">
      <Restrictions>
        <Restriction NameRef="Accountant" Type="Allow"/>
      </Restrictions>
      <Triggers>
        <Trigger NameRef="Paid" Type="Command"/>
      </Triggers>
      <Conditions>
        <Condition Type="Always"/>
      </Conditions>
      <Designer Bending="" X="1211.2500000000002" Y="205"/>
    </Transition>
    <Transition AllowConcatenationType="And" Classifier="Reverse" ConditionsConcatenationType="And" DisableParentStateControl="False" From="AccountantProcessing" IsFork="False" MergeViaSetState="False" Name="AccountantProcessing_R" RestrictConcatenationType="And" To="AuthorConfirmation">
      <Restrictions>
        <Restriction NameRef="Accountant" Type="Allow"/>
      </Restrictions>
      <Triggers>
        <Trigger NameRef="Denial" Type="Command"/>
      </Triggers>
      <Conditions>
        <Condition Type="Always"/>
      </Conditions>
      <Designer Bending="" X="1085" Y="329"/>
    </Transition>
    <Transition AllowConcatenationType="And" Classifier="NotSpecified" ConditionsConcatenationType="And" DisableParentStateControl="False" From="ControllerSighting" IsFork="False" MergeViaSetState="False" Name="ControllerSighting_ControllerSightingExecute_1" RestrictConcatenationType="And" To="ControllerSightingExecute">
      <Triggers>
        <Trigger NameRef="ControllerTimer" Type="Timer"/>
      </Triggers>
      <Conditions>
        <Condition Type="Always"/>
      </Conditions>
      <Designer Bending="" X="544" Y="188"/>
    </Transition>
  </Transitions>
  <CodeActions>
    <CodeAction IsGlobal="False" Name="CheckDocumentHasController" Type="Condition">
      <ActionCode><![CDATA[
importClass(Packages.business.persistence.PersistenceHelper);
importClass(Packages.business.models.Document);

var conditionResult = false;
var doc = PersistenceHelper.getDocument(processInstance.getProcessId());
if (doc != null) {
    conditionResult = doc.getEmployeeControllerId() != null;
}
conditionResult;
]]></ActionCode>
      <Usings><![CDATA[
                ]]></Usings>
    </CodeAction>
    <CodeAction IsGlobal="False" Name="CheckDocumentsAuthorIsBoss" Type="Condition">
      <ActionCode><![CDATA[
importClass(Packages.business.persistence.PersistenceHelper);
importClass(Packages.business.models.Document);
importClass(Packages.business.models.Employee);

var conditionResult = false;
var doc = PersistenceHelper.getDocument(processInstance.getProcessId());
if (doc != null) {
     var emp = PersistenceHelper.getEmployee(doc.getAuthorId());
     if (emp != null) {
        conditionResult = emp.isHead();
        print("isHead: " + conditionResult);
     }
}
conditionResult;
]]></ActionCode>
      <Usings><![CDATA[
                ]]></Usings>
    </CodeAction>
    <CodeAction IsGlobal="False" Name="CheckBigBossMustSight" Type="Condition">
      <ActionCode><![CDATA[
importClass(Packages.business.persistence.PersistenceHelper);
importClass(Packages.business.models.Document);

var conditionResult = false;
var doc = PersistenceHelper.getDocument(processInstance.getProcessId());
if (doc != null) {
     print("Amount: " + doc.getAmount());
     if (doc.getAmount() != null) {
        conditionResult = doc.getAmount().doubleValue() > 100;
        print("doc.getAmount().doubleValue() > 100: " + conditionResult);
     }
}
conditionResult;
]]></ActionCode>
      <Usings><![CDATA[
                ]]></Usings>
    </CodeAction>
  </CodeActions>
  <Localization>
    <Localize Culture="en-US" IsDefault="True" ObjectName="ControllerSighting" Type="State" Value="Controller sighting"/>
    <Localize Culture="en-US" IsDefault="True" ObjectName="AuthorBossSighting" Type="State" Value="Author''''s boss sighting"/>
    <Localize Culture="en-US" IsDefault="True" ObjectName="AuthorConfirmation" Type="State" Value="Author confirmation"/>
    <Localize Culture="en-US" IsDefault="True" ObjectName="BigBossSighting" Type="State" Value="BigBoss sighting"/>
    <Localize Culture="en-US" IsDefault="True" ObjectName="AccountantProcessing" Type="State" Value="Accountant processing"/>
    <Localize Culture="en-US" IsDefault="True" ObjectName="StartProcessing" Type="Command" Value="Start processing"/>
  </Localization>
</Process>

'')')


COMMIT TRANSACTION